package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import views.html.*;

import models.*;

/**
 * Manage a database of Students
 */
public class Application extends Controller {
    
    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
        routes.Application.list(0, "name", "asc", "")
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
    

}
