import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jeng on 16/2/19.
 */
public class RestletClientTest {
    private static final Logger LOG = Logger.getLogger(RestletClientTest.class.getName());

    private RestletClientTest() {
        // static-only access
    }

    public static String getTicket(final String server, final String username,
                                   final String password, final String service) {
        notNull(server, "server must not be null");
        notNull(username, "username must not be null");
        notNull(password, "password must not be null");
        notNull(service, "service must not be null");

        return getServiceTicket(server, getTicketGrantingTicket(server, username,
                password), service);
    }

    private static String getServiceTicket(final String server,
                                           final String ticketGrantingTicket, final String service) {
        if (ticketGrantingTicket == null)
            return null;

        final HttpClient client = new HttpClient();

        final PostMethod post = new PostMethod(server + "/" + ticketGrantingTicket);

        post.setRequestBody(new NameValuePair[]{new NameValuePair("service",
                service)});

        try {
            client.executeMethod(post);

            final String response = post.getResponseBodyAsString();

            switch (post.getStatusCode()) {
                case 200:
                    return response;

                default:
                    LOG.warning("Invalid response code (" + post.getStatusCode()
                            + ") from CAS server!");
                    LOG.info("Response (1k): "
                            + response.substring(0, Math.min(1024, response.length())));
                    break;
            }
        } catch (final IOException e) {
            LOG.warning(e.getMessage());
        } finally {
            post.releaseConnection();
        }

        return null;
    }

    private static String getTicketGrantingTicket(final String server,
                                                  final String username, final String password) {
        final HttpClient client = new HttpClient();

        final PostMethod post = new PostMethod(server);

        post.setRequestBody(new NameValuePair[]{
                new NameValuePair("username", username),
                new NameValuePair("password", password)});

        try {
            client.executeMethod(post);

            final String response = post.getResponseBodyAsString();

            switch (post.getStatusCode()) {
                case 201: {
                    final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*")
                            .matcher(response);

                    if (matcher.matches())
                        return matcher.group(1);

                    LOG
                            .warning("Successful ticket granting request, but no ticket found!");
                    LOG.info("Response (1k): "
                            + response.substring(0, Math.min(1024, response.length())));
                    break;
                }

                default:
                    LOG.warning("Invalid response code (" + post.getStatusCode()
                            + ") from CAS server!");
                    LOG.info("Response (1k): "
                            + response.substring(0, Math.min(1024, response.length())));
                    break;
            }
        } catch (final IOException e) {
            LOG.warning(e.getMessage());
        } finally {
            post.releaseConnection();
        }

        return null;
    }

    private static void notNull(final Object object, final String message) {
        if (object == null)
            throw new IllegalArgumentException(message);
    }

    public static void main(final String[] args) {
        final String server = "http://localhost:8080/cas/v1/tickets";
        final String username = "casuser";
        final String password = "Mellon";
        final String service = "http://www.google.com";

        LOG.info(getTicket(server, username, password, service));
    }
}
