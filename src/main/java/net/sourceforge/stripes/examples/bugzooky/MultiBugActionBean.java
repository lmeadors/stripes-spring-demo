package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.examples.bugzooky.bean.*;
import net.sourceforge.stripes.examples.bugzooky.ext.BugzookyActionBeanContext;
import net.sourceforge.stripes.examples.bugzooky.manager.BugManager;
import net.sourceforge.stripes.examples.bugzooky.manager.ComponentManager;
import net.sourceforge.stripes.examples.bugzooky.manager.PersonManager;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ActionBean that deals with setting up and saving edits to multiple bugs at once. Can also
 * deal with adding multiple new bugs at once.
 *
 * @author Tim Fennell
 */
public class MultiBugActionBean extends BugzookyActionBean {

    // todo: in a real app, you'd inject these.
    private final BugManager bugManager = new BugManager();
    private final ComponentManager componentManager = new ComponentManager();
    private final PersonManager personManager = new PersonManager();

    public static final String BUGZOOKY_BULK_ADD_EDIT = "/WEB-INF/bugzooky/BulkAddEditBugs.jsp";

    // Populated during bulk add/edit operations.
    @ValidateNestedProperties({
            @Validate(field = "shortDescription", required = true, maxlength = 75),
            @Validate(field = "longDescription", required = true, minlength = 25),
            @Validate(field = "component", required = true),
            @Validate(field = "owner", required = true),
            @Validate(field = "priority", required = true)
    })
    private List<Bug> bugs = new ArrayList<Bug>();

    private String view;
    private List<Component> allComponents;
    private List<Person> allPeople;

    @DefaultHandler
    @DontValidate
    public Resolution view() {

        final Resolution resolution;

        if (bugs.isEmpty()) {

            if (isFromForm()) {

                // this happens from the main list page if you select "bulk edit" but haven't selected and items
                final BugzookyActionBeanContext context = getContext();

                context.getValidationErrors().addGlobalError(
                        new SimpleError("You must select at least one bug to edit.")
                );

                // go back to where you came from
                resolution = context.getSourcePageResolution();

            } else {

                // add a few empties
                bugs = Arrays.asList(new Bug[5]);
                resolution = prepareEditForm();

            }

        } else {

            resolution = prepareEditForm();

        }

        return resolution;

    }

    private Resolution prepareEditForm() {

        // these are needed for the edit page
        allComponents = componentManager.getAllComponents();
        allPeople = personManager.getAllPeople();

        return new ForwardResolution(BUGZOOKY_BULK_ADD_EDIT);

    }

    public Resolution save() {

        for (final Bug bug : bugs) {
            bugManager.saveOrUpdate(bug);
        }

        return new RedirectResolution(BugListActionBean.class);

    }

    /**
     * Simple getter that returns the List of Bugs. Note the use of generics syntax - this is
     * necessary to let Stripes know what type of object to create and insert into the list.
     */
    public List<Bug> getBugs() {
        return bugs;
    }

    public void setView(final String view) {
        this.view = view;
    }

    public boolean isFromForm() {
        // Check for the "view" parameter. It will be there if we got here by a form submission.
        return view != null;
    }

    public List<Component> getAllComponents() {
        return allComponents;
    }

    public List<Person> getAllPeople() {
        return allPeople;
    }

}
