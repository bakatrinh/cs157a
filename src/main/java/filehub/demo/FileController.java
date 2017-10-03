package filehub.demo;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class FileController {

    @RequestMapping("file")
    public String main(HttpServletRequest request, Model model) {
        request.getSession().setAttribute("user_id", 1);
        request.getSession().setAttribute("username", "bakatrinh@gmail.com");
        //System.out.println("Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        model.addAttribute("page_name", "File Test Page");
        return "file_page";
    }

    @RequestMapping(value = "file/view/{id}")
    public String file_browser(@PathVariable("id") int group_id, HttpServletRequest request, HttpSession session, Model model) {
        if (!CommonModel.isLoggedIn(request, session)) {
            return "not_logged_in";
        }
        int user_id = (int) session.getAttribute("user_id");
        model.addAttribute("page_name", "File Browser");
        boolean isInGroup = CommonModel.isInGroup(user_id, group_id);
        if (isInGroup) {
            String current_path = "group_files/" + Integer.toString(group_id);
            request.getSession().setAttribute("group_id", group_id);
            request.getSession().setAttribute("root_dir", current_path);
            ArrayList<String> folder_directory = FileModel.getDirectory(current_path);
            request.getSession().setAttribute("current_path", current_path);
            model.addAttribute("folder_directory", folder_directory);
            String group_name = CommonModel.getGroupName(Integer.toString(group_id));
            model.addAttribute("group_name", group_name);
            // in group
            return "file_browser";
        } else {
            // not in group
            return "file_browser_error";
        }
    }

    @RequestMapping(value = {"file/ajax_test"})
    @ResponseBody
    public String ajax_test(HttpServletRequest request, HttpSession session, Model model) {
        if (request.getMethod().equals("POST")) {
            String test = request.getParameter("bob");
            System.out.println(test);
        }
        HashMap<String, String> resultArray = new HashMap<>();
        resultArray.put("status", "success");
        resultArray.put("hello", "there");
        resultArray.put("toastr", "this is a toast message");
        model.addAttribute("extra_attribute", "this is an extra attribute");
        Gson gson = new Gson();
        System.out.println(gson.toJson(resultArray));
        return gson.toJson(resultArray);
    }

    @RequestMapping(value = {"file/test"})
    public String test(HttpServletRequest request, HttpSession session, Model model) {
        String path = "group_files/1/bobcat";
        String new_path = "group_files/1/jordanboy";
        ArrayList<ArrayList<String>> temp = FileModel.getSubfilesAndFoldersWithNewPath(path, new_path);
        for (ArrayList<String> e : temp) {
            for (String s : e) {
                System.out.println(s);
            }
            System.out.println("-------------");
        }
        return "";
    }

    @RequestMapping(value = {"file/refresh_files_table"})
    public String refresh_files_table(HttpSession session, Model model) {
        String current_path = (String) session.getAttribute("current_path");
        ArrayList<String> folder_directory = FileModel.getDirectory(current_path);
        model.addAttribute("folder_directory", folder_directory);
        return "includes_files_table";
    }

    @RequestMapping(value = "file/open_folder_ajax")
    public String open_folder_ajax(HttpServletRequest request, HttpSession session, Model model) {
        if (CommonModel.isLoggedIn(request, session) && request.getMethod().equals("POST") && request.getParameter("id") != null) {
            int user_id = (int) session.getAttribute("user_id");
            int group_id = (int) session.getAttribute("group_id");
            String id = request.getParameter("id").trim();
            boolean isInGroup = CommonModel.isInGroup(user_id, group_id);
            if (isInGroup) {
                String current_path = FileModel.getFilePathByID(id);
                request.getSession().setAttribute("current_path", current_path);
                ArrayList<String> folder_directory = FileModel.getDirectory(current_path);
                model.addAttribute("folder_directory", folder_directory);
                return "includes_files_table";
            }
        }
        return null;
    }

    @RequestMapping(value = "file/previous_folder_ajax")
    public String previous_folder_ajax(HttpServletRequest request, HttpSession session, Model model) {
        if (CommonModel.isLoggedIn(request, session)) {
            int user_id = (int) session.getAttribute("user_id");
            int group_id = (int) session.getAttribute("group_id");
            boolean isInGroup = CommonModel.isInGroup(user_id, group_id);
            if (isInGroup) {
                String current_path = (String) session.getAttribute("current_path");
                if (!FileModel.isInRootDIR(session, current_path)) {
                    String previous_path = FileModel.getPreviousFolderPath(current_path);
                    request.getSession().setAttribute("current_path", previous_path);
                    ArrayList<String> folder_directory = FileModel.getDirectory(previous_path);
                    model.addAttribute("folder_directory", folder_directory);
                    return "includes_files_table";
                }
            }
        }
        return null;
    }

    @RequestMapping(value = {"file/exit_new_folder_html_ajax"})
    public String exit_new_folder_html_ajax(HttpSession session, Model model) {
        return "includes_files_table_header";
    }

    @RequestMapping(value = {"file/add_new_folder_html_ajax"})
    public String add_new_folder_html_ajax(HttpSession session, Model model) {
        return "includes_files_table_header_edit";
    }

    @RequestMapping(value = {"file/add_new_folder_submit_ajax"})
    @ResponseBody
    public String add_new_folder_submit_ajax(HttpServletRequest request, HttpSession session, Model model) {
        HashMap<String, String> resultArray = new HashMap<>();
        Gson gson = new Gson();
        if (CommonModel.isLoggedIn(request, session) && request.getMethod().equals("POST") && request.getParameter("folder_name") != null) {
            int user_id = (int) session.getAttribute("user_id");
            int group_id = (int) session.getAttribute("group_id");
            String folder_name = request.getParameter("folder_name").trim();
            if (!CommonModel.isLettersNumbersUnderscoreSpaceOnlyString(folder_name)) {
                resultArray.put("status", "failed");
                resultArray.put("error", "The specified folder: <b>" + folder_name + "</b> can only contain letters, numbers, and underscores (no space).");
                return gson.toJson(resultArray);
            } else if (FileModel.isFolderAlreadyExist(session, folder_name)) {
                resultArray.put("status", "failed");
                resultArray.put("error", "The specified folder: <b>" + folder_name + "</b> already exist.");
                return gson.toJson(resultArray);
            } else if (FileModel.isAllowedAddNewFolder(user_id, group_id)) {
                boolean newFolderCheck = FileModel.createNewFolder(session, folder_name);
                if (newFolderCheck) {
                    resultArray.put("status", "success");
                    resultArray.put("toastr", "New Folder Created");
                    return gson.toJson(resultArray);
                }
            }
        }
        resultArray.put("status", "failed");
        resultArray.put("error", "Unable to create folder. You may need higher access.");
        return gson.toJson(resultArray);
    }

    @RequestMapping(value = {"file/delete_folder_submit_ajax"})
    @ResponseBody
    public String delete_folder_submit_ajax(HttpServletRequest request, HttpSession session, Model model) {
        HashMap<String, String> resultArray = new HashMap<>();
        Gson gson = new Gson();
        if (CommonModel.isLoggedIn(request, session) && request.getMethod().equals("POST") && request.getParameter("id") != null) {
            int user_id = (int) session.getAttribute("user_id");
            int group_id = (int) session.getAttribute("group_id");
            String id = request.getParameter("id").trim();
            if (FileModel.isAllowedDeleteFolder(user_id, group_id)) {
                boolean folderDeletedCheck = FileModel.deleteFolder(session, id);
                if (folderDeletedCheck) {
                    resultArray.put("status", "success");
                    resultArray.put("toastr", "Folder Deleted");
                    return gson.toJson(resultArray);
                }
            }
        }
        resultArray.put("status", "failed");
        resultArray.put("error", "Unable to delete folder. You may need higher access.");
        return gson.toJson(resultArray);
    }

    @RequestMapping(value = {"file/rename_folder_submit_ajax"})
    @ResponseBody
    public String rename_folder_submit_ajax(HttpServletRequest request, HttpSession session, Model model) {
        HashMap<String, String> resultArray = new HashMap<>();
        Gson gson = new Gson();
        if (CommonModel.isLoggedIn(request, session) && request.getMethod().equals("POST") && request.getParameter("id") != null
                && request.getParameter("folder_name") != null) {
            int user_id = (int) session.getAttribute("user_id");
            int group_id = (int) session.getAttribute("group_id");
            String id = request.getParameter("id").trim();
            String folder_name = request.getParameter("folder_name").trim();
            if (folder_name == null || folder_name == "") {
                resultArray.put("status", "failed");
                resultArray.put("error", "Folder name cannot be blank.");
                return gson.toJson(resultArray);
            }
            if (!CommonModel.isLettersNumbersUnderscoreSpaceOnlyString(folder_name)) {
                resultArray.put("status", "failed");
                resultArray.put("error", "The specified folder: <b>" + folder_name + "</b> can only contain letters, numbers, and underscores (no space).");
                return gson.toJson(resultArray);
            }
            if (FileModel.isAllowedRenameFolder(user_id, group_id)) {
                if (FileModel.isFolderNameTheSame(id, folder_name)) {
                    resultArray.put("status", "success");
                    return gson.toJson(resultArray);
                } else if (FileModel.isFolderAlreadyExist(session, folder_name)) {
                    resultArray.put("status", "fail");
                    resultArray.put("error", "The folder name: <b>" + folder_name + "</b> is already taken.");
                    return gson.toJson(resultArray);
                } else {
                    boolean folderRenameCheck = FileModel.renameFolder(session, id, folder_name);
                    if (folderRenameCheck) {
                        resultArray.put("status", "success");
                        resultArray.put("toastr", "Folder Renamed Successfully");
                        return gson.toJson(resultArray);
                    }
                }
            }
        }
        resultArray.put("status", "failed");
        resultArray.put("swal_error", "Unable to rename folder. You may need higher access.");
        return gson.toJson(resultArray);
    }

    @RequestMapping(value = {"file/edit_notes_submit_ajax"})
    @ResponseBody
    public String edit_notes_submit_ajax(HttpServletRequest request, HttpSession session, Model model) {
        HashMap<String, String> resultArray = new HashMap<>();
        Gson gson = new Gson();
        if (CommonModel.isLoggedIn(request, session) && request.getMethod().equals("POST") && request.getParameter("id") != null
                && request.getParameter("notes") != null) {
            int user_id = (int) session.getAttribute("user_id");
            int group_id = (int) session.getAttribute("group_id");
            String id = request.getParameter("id").trim();
            String notes = request.getParameter("notes").trim();
            if (FileModel.isAllowedEditNotes(user_id, group_id)) {
                boolean notesEditCheck = FileModel.editNotes(session, id, notes);
                if (notesEditCheck) {
                    resultArray.put("status", "success");
                    resultArray.put("toastr", "Notes Updated Successfully");
                    return gson.toJson(resultArray);
                }
            }
        }
        resultArray.put("status", "failed");
        resultArray.put("swal_error", "Unable to edit notes. You may need higher access.");
        return gson.toJson(resultArray);
    }
}