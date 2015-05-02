package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Student;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.createForm;
import views.html.editForm;
import views.html.list;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import java.io.File;
import java.nio.file.StandardCopyOption;

import static play.data.Form.form;

/**
 * Manage a database of Students
 */
public class Application extends Controller {

    public static int lectureNo;
    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
            controllers.routes.Application.list(0, "name", "asc", "", 0)
    );

    /**
     * Handle default path requests, redirect to students list
     */
    public static Result index() {
        return GO_HOME;
    }

    /**
     * Display the paginated list of students.
     *
     * @param page   Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order  Sort order (either asc or desc)
     * @param filter Filter applied on students names
     */
    public static Result list(int page, String sortBy, String order, String filter, int lectureNo) {
        Application.lectureNo = lectureNo;
        return ok(
                list.render(
                        Student.page(page, 20, sortBy, order, filter),
                        sortBy, order, filter, lectureNo
                )
        );
    }

    /**
     * Display the 'edit form' of a existing Student.
     *
     * @param id Id of the student to edit
     */
    public static Result edit(Long id) {
        Form<Student> studentForm = form(Student.class).fill(
                Student.find.byId(id)
        );
        return ok(
                editForm.render(id, studentForm)
        );
    }

    /**
     * Handle the 'edit form' submission
     *
     * @param id Id of the student to edit
     */
    public static Result update(Long id) {
        Form<Student> studentForm = form(Student.class).bindFromRequest();
        if (studentForm.hasErrors()) {
            return badRequest(editForm.render(id, studentForm));
        }
        studentForm.get().update(id);
        flash("success", "Student " + studentForm.get().name + " has been updated");
        return GO_HOME;
    }


    /**
     * Display the 'new student form'.
     */
    public static Result create() {
        Form<Student> studentForm = form(Student.class);
        return ok(
                createForm.render(studentForm)
        );
    }

    /**
     * Handle the 'new Student form' submission
     */
    public static Result save() {
        Form<Student> studentForm = form(Student.class).bindFromRequest();
        if (studentForm.hasErrors()) {
            return badRequest(createForm.render(studentForm));
        }
        studentForm.get().save();
        flash("success", "Student " + studentForm.get().name + " has been created");
        return GO_HOME;
    }

    /**
     * Handle student deletion
     */
    public static Result delete(Long id) {
        Student.find.ref(id).delete();
        flash("success", "Student has been deleted");
        return GO_HOME;
    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result takeAttendance() {
        JsonNode json = request().body().asJson();
        String macAddress, date;
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            macAddress = json.findPath("macAddress").textValue();
            date = json.findPath("date").textValue();
            if (macAddress == null || date == null) {
                return badRequest("Missing parameter");
            }
        }

        Student student = Student.findByMac(macAddress);
        ObjectNode result = Json.newObject();
        if (student != null) {
            Student.recordAttendance(student, date, Application.lectureNo);
            result.put("name", student.name);
            result.put("date", date);
            result.put("lecture#", Application.lectureNo);
            result.put("status", "success");
        } else {
            result.put("status", "failed");
        }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result signup() {
        JsonNode json = request().body().asJson();
        Long student_id;
        String macAddress;
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            student_id = json.findPath("id").asLong();
            macAddress = json.findPath("macAddress").textValue();
            if (student_id == null || macAddress == null) {
                return badRequest("Missing parameter");
            }
        }
        ObjectNode result = Json.newObject();
        Student student = Student.findByStudent_id(student_id);
        if (student != null) {
            Student.recordMac(student, macAddress);
            result.put("name", student.name);
            result.put("id", student_id);
            result.put("macAddress", macAddress);
            result.put("status", "success");
        } else {
            result.put("status", "failed");
        }
        return ok(result);
    }

//    public static Result upload() {
//        MultipartFormData body = request().body().asMultipartFormData();
//        FilePart picture = body.getFile("picture");
//        if (picture != null) {
//            String fileName = picture.getFilename();
//            String contentType = picture.getContentType();
//            File file = picture.getFile();
//            String uploadLocation = Play.application().configuration().getString("upload.path");
//
//            if (file.renameTo(new File(uploadLocation + "/" +fileName))) {
//                System.out.println("File is moved successful!");
//                System.out.println(uploadLocation + "/" +fileName);
//            } else {
//                System.out.println("File is failed to move!");
//            }
//
//            return ok("File uploaded");
//        } else {
//            flash("error", "Missing file");
//            return redirect(routes.Application.index());
//        }
//    }
}
