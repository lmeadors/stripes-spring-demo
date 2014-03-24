package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.util.HtmlUtil;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

@UrlBinding("/bugzooky/view{resource}")
public class ViewResourceActionBean extends BugzookyActionBean {

    @Validate(required = true)
    private String resource;

    /**
     * Validates that only resources in the allowed places are asked for.
     */
    @ValidationMethod
    public void validate(ValidationErrors errors) {
        if (resource.startsWith("/WEB-INF") && !resource.startsWith("/WEB-INF/src") && !resource.endsWith("jsp")) {
            errors.add(
                    "resource",
                    new SimpleError("Naughty, naughty. We mustn't hack the URL now.")
            );
        }
    }

    /**
     * Handler method which will handle a request for a resource in the web application
     * and stream it back to the client inside of an HTML preformatted section.
     */
    public Resolution view() {

        final InputStream stream = getContext().getServletContext().getResourceAsStream(this.resource);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return new Resolution() {
            public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                final PrintWriter writer = response.getWriter();
                writer.write("<html><head><title>");
                writer.write(resource);
                writer.write("</title></head><body><pre>");

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(HtmlUtil.encode(line));
                    writer.write("\n");
                }

                writer.write("</pre></body></html>");
            }
        };
    }

    /**
     * Method used when this ActionBean is used as a view helper. Returns a listing of all the
     * JSPs and ActionBeans available for viewing.
     */
    public Collection getAvailableResources() {

        final ServletContext context = getContext().getServletContext();

        final SortedSet<String> resources = new TreeSet<String>();
        resources.addAll(getResourcePaths(context, "/WEB-INF/bugzooky/"));
        resources.addAll(getResourcePaths(context, "/WEB-INF/bugzooky/layout/"));
        resources.addAll(getResourcePaths(context, "/WEB-INF/src/"));

        final Iterator<String> iterator = resources.iterator();

        while (iterator.hasNext()) {
            final String file = iterator.next();
            if (!file.endsWith(".jsp") && !file.endsWith(".java")) {
                iterator.remove();
            }
        }

        return resources;

    }

    @SuppressWarnings("unchecked")
    private Set<String> getResourcePaths(ServletContext servletContext, String path) {
        return (Set<String>) servletContext.getResourcePaths(path);
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

}
