package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Student;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.createForm;
import views.html.editForm;
import views.html.list;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.BodyParser;

import static play.data.Form.form;

/**
 * Manage a database of Students
 */
public class Application extends Controller {
    
    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
        controllers.routes.Application.list(0, "name", "asc", "")
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
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on students names
     */
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(
            list.render(
                Student.page(page, 20, sortBy, order, filter),
                sortBy, order, filter
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
        if(studentForm.hasErrors()) {
            return badRequest(editForm.render(id, studentForm));
        }
        studentForm.get().update(id);
        flash("success", "Student " + studentForm.get().name + " has been updated");
        return GO_HOME;
    }


    /**
     * Handle the 'Absence check via Android App' submission
     *
     * @param mac Mac Address of the student to edit
     */
    public static Result updateViaAndroidApp(String mac, boolean absence) {
        Form<Student> studentForm = form(Student.class).bindFromRequest();
        studentForm.get().update(mac);
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
        if(studentForm.hasErrors()) {
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
    public static Result takeAttendance(){
        JsonNode json = request().body().asJson();
        String macAddress, date;
        if(json == null) {
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
        if(student != null){
            Student.recordAttendance(student,date);
            result.put("name", student.name);
            result.put("date", date);
            result.put("status", "success");
        }else {
            result.put("status", "failed");
        }
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result signup(){
        JsonNode json = request().body().asJson();
        Long student_id;
        String macAddress;
        if(json == null) {
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
        if(student != null){
            Student.recordMac(student,macAddress);
            result.put("name", student.name);
            result.put("id", student_id);
            result.put("macAddress", macAddress);
            result.put("status", "success");
        }else{
            result.put("status", "failed");
        }
        return ok(result);
    }
}
