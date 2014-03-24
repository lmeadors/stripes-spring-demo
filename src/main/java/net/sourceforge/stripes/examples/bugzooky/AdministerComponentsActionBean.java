package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.examples.bugzooky.biz.Component;
import net.sourceforge.stripes.examples.bugzooky.biz.ComponentManager;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import java.util.List;

/**
 * Manages the administration of Components, from the Administer Bugzooky page. Receives a List
 * of Components, which may include a new component and persists the changes. Also receives an
 * Array of IDs for components that are to be deleted, and deletes those.
 *
 * @author Tim Fennell
 */
public class AdministerComponentsActionBean extends BugzookyActionBean {

    public static final String ADMINISTER_BUGZOOKY_JSP = "/WEB-INF/bugzooky/AdministerBugzooky.jsp";

    // todo: in a real app, this would be injected
    private final ComponentManager componentManager = new ComponentManager();

    private int[] deleteIds;

    @ValidateNestedProperties({
            @Validate(field = "name", required = true, minlength = 3, maxlength = 25)
    })
    private List<Component> components;

    @DefaultHandler
    @DontBind
    public Resolution view() {
        components = componentManager.getAllComponents();
        return new ForwardResolution(ADMINISTER_BUGZOOKY_JSP);
    }

    public Resolution save() {

        // Save any changes to existing components (and create new ones)
        for (final Component component : components) {
            componentManager.saveOrUpdate(component);
        }

        // Then, if the user checked anyone off to be deleted, delete them
        if (deleteIds != null) {
            for (final int id : deleteIds) {
                componentManager.deleteComponent(id);
            }
        }

        return new RedirectResolution(getClass());

    }

    public int[] getDeleteIds() {
        return deleteIds;
    }

    public void setDeleteIds(int[] deleteIds) {
        this.deleteIds = deleteIds;
    }

    /**
     * If no list of components is set and we're not handling the "save" event then populate the
     * list of components and return it.
     */
    public List<Component> getComponents() {

        if (components == null && !"save".equals(getContext().getEventName())) {
            // this can be used as a view helper
            // todo: refactor this to a separate class?
            components = componentManager.getAllComponents();
        }

        return components;

    }

    // this isn't used?
//    public void setComponents(List<Component> components) {
//        this.components = components;
//    }

}
