package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.examples.bugzooky.biz.Person;
import net.sourceforge.stripes.examples.bugzooky.biz.PersonManager;
import net.sourceforge.stripes.examples.bugzooky.ext.Public;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationError;

/**
 * An example of an ActionBean that uses validation annotations on fields instead of
 * on methods.  Logs the user in using a conventional username/password combo and
 * validates the password in the action method.
 */
@Public
public class LoginActionBean extends BugzookyActionBean {

    public static final String LOGIN_JSP = "/WEB-INF/bugzooky/Login.jsp";

    private final PersonManager pm = new PersonManager();

    @Validate(required = true)
    private String username;

    @Validate(required = true)
    private String password;

    private String targetUrl;

    @DefaultHandler
    @DontValidate
    public Resolution view() {
        return new ForwardResolution(LOGIN_JSP);
    }

    public Resolution login() {

        final Person person = pm.getPerson(this.username);

        if (person == null) {

            final ValidationError error = new LocalizableError("usernameDoesNotExist");
            getContext().getValidationErrors().add("username", error);
            return getContext().getSourcePageResolution();

        } else if (!person.getPassword().equals(password)) {

            final ValidationError error = new LocalizableError("incorrectPassword");
            getContext().getValidationErrors().add("password", error);
            return getContext().getSourcePageResolution();

        } else {

            getContext().setUser(person);

            if (this.targetUrl != null) {
                return new RedirectResolution(this.targetUrl);
            } else {
                return new RedirectResolution(BugListActionBean.class);
            }

        }

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

}
