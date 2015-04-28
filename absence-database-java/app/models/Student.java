package models;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

import com.avaje.ebean.*;

/**
 * Student entity managed by Ahmad
 */
@Entity 
public class Student extends Model {

    private static final long serialVersionUID = 1L;

	@Id
    public Long id;
    
    @Constraints.Required
    public String name;
    
    public String mac;
    
    public Boolean one;
    public Boolean two;
    public Boolean three;
    public Boolean four;
    public Boolean five;
    public Boolean six;
    public Boolean seven;
    public Boolean eight;


    /**
     * Generic query helper for entity Computer with id Long
     */
    public static Finder<Long, Student> find = new Finder<Long, Student>(Long.class, Student.class);
    
    /**
     * Return a page of computer
     *
     * @param page Page to display
     * @param pageSize Number of computers per page
     * @param sortBy Computer property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public static Page<Student> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
    }
    
}