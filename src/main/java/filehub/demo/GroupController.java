package filehub.demo;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Controller
public class GroupController {

    @RequestMapping("group")
    public String main(HttpSession session, Model model) {
        //System.out.println("hi there"+ UUID.randomUUID());
        model.addAttribute("page_name", "Group Page 01");
        //System.out.println("group count "+ GroupModel.countGroup((int) session.getAttribute("user_id")));
        return "group_page";
    }

    @RequestMapping("group/create_group")
    public String createGroup(Model model) {
        model.addAttribute("page_name", "Create Group");
        return "group_create";
    }

    @RequestMapping("group/create_group/add")
    public String addGroup(HttpServletRequest request, HttpSession session, Model model) {
        model.addAttribute("page_name", "Create Group");
        String groupname = request.getParameter("group_name");
        String groupPassword = request.getParameter("group_password");
        if (request.getMethod().equals("POST")) {
            int userID = (int) session.getAttribute("user_id");
            //System.out.println("group name: " + groupname);
            //System.out.println("group pass: " + groupPassword);
            GroupModel.insertGroup(groupname, userID, groupPassword);

        }
        return "redirect:/group";
    }

    @RequestMapping("group/all")
    public String listGroups(HttpServletRequest request, HttpSession session, Model model) {
        ArrayList<Groups> allGroup = GroupModel.getAllGroup();
        for (Groups e : allGroup) {
            System.out.println(e);
        }
        System.out.println("group count " + GroupModel.countGroup((int) session.getAttribute("user_id")));
        model.addAttribute("page_name", "View all group");

        return "group_page";
    }

    @RequestMapping(value = "/group/confirmpass/{groupName}/{input}", method = RequestMethod.GET)
    @ResponseBody
    public boolean confirmGroupPass(@PathVariable("groupName") String groupName, @PathVariable("input") String input, HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException {
        return GroupModel.isGroupPassCorrect(groupName, input);
    }

    @RequestMapping(value = {"/group/delete_group"}, method = RequestMethod.POST)
    @ResponseBody
    public String delete_group(HttpServletRequest request, HttpSession session) {
        HashMap<String, String> resultArray = new HashMap<>();
        Gson gson = new Gson();
        if (CommonModel.isLoggedIn(request, session) && request.getMethod().equals("POST") && request.getParameter("groupId") != null && request.getParameter("groupOwner") != null) {
            int groupOwner = Integer.valueOf(request.getParameter("groupOwner").trim());
            int groupId = Integer.valueOf(request.getParameter("groupId").trim());

            System.out.println("group owner = " + groupOwner);
            System.out.println("group id = " + groupId);

            if (GroupModel.deleteGroup(groupOwner, groupId)) {
                resultArray.put("status", "success");
                resultArray.put("title", "Success");
                resultArray.put("content", "Deleted Successfully!");
            } else {
                resultArray.put("status", "failed");
                resultArray.put("title", "Failed");
                resultArray.put("content", "Deleted failed!");
            }
        } else {
            resultArray.put("status", "failed");
            resultArray.put("title", "Failed");
            resultArray.put("content", "Deleted failed!");
        }

        System.out.println(gson.toJson(resultArray).toString());
        return gson.toJson(resultArray);
    }


    @RequestMapping(value = {"/group/refresh_group_table"})
    public String refresh_group_table(HttpServletRequest request, HttpSession session, Model model) {

        if (CommonModel.isLoggedIn(request, session)) {
            ArrayList<Groups> groups = GroupModel.getAllGroups((int) session.getAttribute("user_id"));
            model.addAttribute("user_id", (int) session.getAttribute("user_id"));
            model.addAttribute("groups", groups);
            System.out.println("/group/refresh_group_table");
            for (Groups g : groups) {
                System.out.println("name " + g.getGroup_name());
            }
            return "includes_group_table";
        } else {
            return null;
        }
    }

    @RequestMapping(value = {"group/send_group_invite"})
    public String send_group_invite(HttpServletRequest request, HttpSession session, Model model) {
        if (!CommonModel.isLoggedIn(request, session)) {
            model.addAttribute("error_message", "You are not logged in");
            return "file_url_modal_error";
        } else if (request.getMethod().equals("POST") && request.getParameter("group_id") != null && request.getParameter("group_name") != null) {
            String group_id = request.getParameter("group_id");
            model.addAttribute("group_id", group_id);
            String group_name = request.getParameter("group_name");
            model.addAttribute("group_name", group_name);
            return "invite_to_group_modal";
        } else {
            model.addAttribute("error_message", "Internal Error. Please Contact an Admin");
            return "file_url_modal_error";
        }
    }

    @RequestMapping(value = {"group/send_group_invite_submit"})
    @ResponseBody
    public String send_group_invite_submit(HttpServletRequest request, HttpSession session) {
        HashMap<String, String> resultArray = new HashMap<>();
        Gson gson = new Gson();
        if (CommonModel.isLoggedIn(request, session) && request.getMethod().equals("POST") && request.getParameter("group_id") != null && request.getParameter("send_to_email") != null && request.getParameter("invite_access_level") != null) {
            String send_to_email = request.getParameter("send_to_email");
            String send_to_id = CommonModel.getUserIDByEmail(send_to_email);
            String group_id = request.getParameter("group_id");
            String invite_access_level = request.getParameter("invite_access_level");
            if (CommonModel.isInGroup(Integer.parseInt(send_to_id), Integer.parseInt(group_id))) {
                HashMap<String, String> error_array = new HashMap<>();
                error_array.put("send_to_email", "User is already in the group");
                String error_array_gson = gson.toJson(error_array);
                resultArray.put("status", "fail");
                resultArray.put("error", error_array_gson);
                return gson.toJson(resultArray);
            } else {
                int user_id = (int) session.getAttribute("user_id");
                String getGroupInviteCode = GroupModel.getGroupInviteCode(Integer.toString(user_id), send_to_id, group_id, invite_access_level);
                String user_full_name = CommonModel.getFullName(Integer.toString(user_id));
                String group_name = CommonModel.getGroupName(group_id);
                String message = user_full_name + " has invited you to join the group: <b>" + group_name + "</b><br>" + "Visit the following page to join:<br><a href=\"/group/invite_code/"+getGroupInviteCode+"\">Click Here</a>";
                MessagingModel.insertNewMessage(Integer.toString(user_id), send_to_id, message);
                resultArray.put("status", "success");
                resultArray.put("toastr", "A group invite has been sent to the user.");
                return gson.toJson(resultArray);
            }
        }
        resultArray.put("status", "failed");
        resultArray.put("swal_error", "Unable to send invite. Internal Error.");
        return gson.toJson(resultArray);
    }

    @RequestMapping(value = "group/invite_code/{invite_code}")
    public String file_browser(@PathVariable("invite_code") String invite_code, HttpServletRequest request, HttpSession session, Model model) {
        model.addAttribute("page_name", "Group Invite Page");
        if (!CommonModel.isLoggedIn(request, session)) {
            return "not_logged_in";
        }
        int user_id = (int) session.getAttribute("user_id");
        ArrayList<String> getCodeData = GroupModel.getCodeData(Integer.toString(user_id), invite_code);
        if (getCodeData != null && !getCodeData.isEmpty()) {
            String group_id = getCodeData.get(4);
            if (CommonModel.isInGroup(user_id, Integer.parseInt(group_id))) {
                model.addAttribute("error_message", "You have already joined this group");
                return "generic_error_page";
            }
            else {
                String invite_access_level = getCodeData.get(5);
                GroupModel.addNewMemberByInviteCode(Integer.toString(user_id), group_id, invite_access_level);
                GroupModel.removeAllInvitesByUserIDAndGroup(Integer.toString(user_id), group_id);
                model.addAttribute("group_name", CommonModel.getGroupName(group_id));
                model.addAttribute("group_id", group_id);
                return "group_invite_success";
            }
        }
        else {
            model.addAttribute("error_message", "This URL is no longer valid.");
            return "generic_error_page";
        }
    }
}