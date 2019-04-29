package io.slingr.endpoints.freshbooks;

import io.slingr.endpoints.services.exchange.Parameter;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.utils.Strings;
import io.slingr.endpoints.utils.tests.EndpointTests;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * <p>Test over the FreshbooksEndpoint class
 *
 * <p>Created by lefunes on 10/01/15.
 */
@Ignore("For dev proposes")
public class FreshbooksEndpointTest {
    private static final Logger logger = LoggerFactory.getLogger(FreshbooksEndpointTest.class);

    private static EndpointTests test;

    @BeforeClass
    public static void init() throws Exception {
        test = EndpointTests.start(new io.slingr.endpoints.freshbooks.Runner(), "test.properties");
    }

    @Test
    public void testClientFunctions() throws Exception {
        final String email = "test.integrations@slingr.io";
        logger.info(String.format("-- test email [%s]", email));

        final String invoiceNumber = "I-"+ Strings.randomAlphabetic(5);
        logger.info(String.format("-- test invoice [%s]", invoiceNumber));

        logger.info("-- check invalid client");
        checkDeletedClient("123456789");

        int[] ini = checkTotal(email, -1, -1);
        final int initial = ini[0];
        final int initialEmail = ini[1];

        logger.info("-- create client ");
        final String id = createClient(email, "Test", "U001");
        logger.info(String.format("---- client id [%s]", id));

        logger.info("-- check client ");
        checkClientName(id, email, "Test", "U001");

        checkTotal(email, initial + 1, initialEmail + 1);

        logger.info("-- update client ");
        updateClient(id, "Test", "U002");

        logger.info("-- check client ");
        checkClientName(id, email, "Test", "U002");

        checkTotal(email, initial + 1, initialEmail + 1);

        logger.info("-- check invoices ");
        checkInvoiceTotal(id, 0);

        logger.info("-- create invoice ");
        final String invoiceId = createInvoice(id, invoiceNumber, 4);
        logger.info(String.format("---- invoice id [%s]", invoiceId));

        checkInvoiceById(invoiceId, invoiceNumber, 4);

        logger.info("-- check invoices ");
        checkInvoiceTotal(id, 1);

        logger.info("-- update invoice ");
        updateInvoice(invoiceId, 2);

        checkInvoiceById(invoiceId, invoiceNumber, 2);

        logger.info("-- check invoices ");
        checkInvoiceTotal(id, 1);

        logger.info("-- remove invoice ");
        removeInvoice(invoiceId);

        logger.info("-- check invoices ");
        checkInvoiceTotal(id, 0);

        logger.info("-- check recurring profiles ");
        checkRecurringTotal(id, 0);

        logger.info("-- create recurring profile ");
        final String recurringId = createRecurring(id, 4);
        logger.info(String.format("---- recurring id [%s]", recurringId));

        checkRecurringById(recurringId, 4);

        logger.info("-- check recurring profiles ");
        checkRecurringTotal(id, 1);

        logger.info("-- update recurring profile ");
        updateRecurring(recurringId, 2);

        checkRecurringById(recurringId, 2);

        logger.info("-- check recurring profiles ");
        checkRecurringTotal(id, 1);

        logger.info("-- remove recurring profile ");
        removeRecurring(recurringId);

        logger.info("-- check recurring profiles ");
        checkRecurringTotal(id, 0);

        logger.info("-- remove client ");
        removeClient(id);

        checkTotal(email, initial, initialEmail);

        logger.info("-- check deleted client");
        checkDeletedClient(id);

        logger.info("-- END");
    }

    private String createClient(String email, String firstName, String lastName) throws Exception {
        final Json data = Json.map()
                .set("email", email)
                .set("first_name", firstName)
                .set("last_name", lastName);

        final Json response = test.executeFunction( "createClient", data);
        checkResponse(response);
        assertTrue(response.json("response").contains("client_id"));

        return response.json("response").string("client_id");
    }

    private String createInvoice(String clientId, String number, int linesSize) throws Exception {
        final List<Json> lines = new ArrayList<>(linesSize);
        for (int i = 0; i < linesSize; i++) {
            lines.add(Json.map()
                            .set("name", "Product " + (i + 1))
                            .set("unit_cost", 1)
                            .set("quantity", 1)
            );
        }

        final Json data = Json.map()
                .set("client_id", clientId)
                .set("number", number)
                .set("lines", Json.map()
                                .set("line", lines)
                );

        final Json response = test.executeFunction( "createInvoice", data);
        checkResponse(response);
        assertTrue(response.json("response").contains("invoice_id"));

        return response.json("response").string("invoice_id");
    }

    private String createRecurring(String clientId, int linesSize) throws Exception {
        final List<Json> lines = new ArrayList<>(linesSize);
        for (int i = 0; i < linesSize; i++) {
            lines.add(Json.map()
                    .set("name", "Product " + (i + 1))
                            .set("unit_cost", 1)
                    .set("quantity", 1)
            );
        }

        final Json data = Json.map()
                .set("client_id", clientId)
                .set("lines", Json.map()
                                .set("line", lines)
                );

        final Json response = test.executeFunction( "createRecurring", data);
        checkResponse(response);
        assertTrue(response.json("response").contains("recurring_id"));

        return response.json("response").string("recurring_id");
    }

    private void updateClient(String id, String firstName, String lastName) throws Exception {
        final Json data = Json.map()
                .set("client_id", id)
                .set("first_name", firstName)
                .set("last_name", lastName);

        final Json response = test.executeFunction( "updateClient", data);
        checkResponse(response);
    }

    private void updateInvoice(String id, int linesSize) throws Exception {

        final List<Json> lines = new ArrayList<>(linesSize);
        for (int i = 0; i < linesSize; i++) {
            lines.add(Json.map()
                            .set("name", "Updated Product " + (i + 1))
                            .set("unit_cost", 1.75)
                            .set("quantity", 1)
            );
        }

        final Json data = Json.map()
                .set("invoice_id", id)
                .set("lines",
                        Json.map().set("line", lines)
                );

        final Json response = test.executeFunction( "updateInvoice", data);
        checkResponse(response);
    }

    private void updateRecurring(String id, int linesSize) throws Exception {

        final List<Json> lines = new ArrayList<>(linesSize);
        for (int i = 0; i < linesSize; i++) {
            lines.add(Json.map()
                            .set("name", "Updated Product " + (i + 1))
                            .set("unit_cost", 1.75)
                            .set("quantity", 1)
            );
        }

        final Json data = Json.map()
                .set("recurring_id", id)
                .set("lines",
                        Json.map().set("line", lines)
                );

        final Json response = test.executeFunction( "updateRecurring", data);
        checkResponse(response);
    }

    private void removeClient(String id) throws Exception {
        final Json data = Json.map()
                .set("client_id", id);

        final Json response = test.executeFunction( "removeClient", data);
        checkResponse(response);
    }

    private void removeInvoice(String id) throws Exception {
        final Json data = Json.map()
                .set("invoice_id", id);

        final Json response = test.executeFunction( "removeInvoice", data);
        checkResponse(response);
    }

    private void removeRecurring(String id) throws Exception {
        final Json data = Json.map()
                .set("recurring_id", id);

        final Json response = test.executeFunction( "removeRecurring", data);
        checkResponse(response);
    }

    private int[] checkTotal(String email, int expectedTotal, int expectedEmailTotal) throws Exception {
        Json response;
        logger.info("-- find clients ");
        response = findAll();

        final int total = response.integer("@total");
        if(expectedTotal >= 0) {
            assertEquals(expectedTotal, total);
        }
        logger.info(String.format("---- total clients [%s]", total));

        logger.info("-- find email clients ");
        response = findByEmail(email);

        final int emailTotal = response.integer("@total");
        if(expectedEmailTotal >= 0) {
            assertEquals(expectedEmailTotal, emailTotal);
        }
        logger.info(String.format("---- total email clients [%s]", emailTotal));

        return new int[]{total, emailTotal};
    }

    private void checkInvoiceTotal(String id, int expectedTotal) throws Exception {
        Json response;
        logger.info("-- find invoices ");
        response = findInvoicesByClient(id);

        final int total = response.integer("@total");
        assertEquals(expectedTotal, total);
        logger.info(String.format("---- total invoices [%s]", total));
    }

    private void checkRecurringTotal(String id, int expectedTotal) throws Exception {
        Json response;
        logger.info("-- find recurring profiles ");
        response = findRecurringByClient(id);

        final int total = response.integer("@total");
        assertEquals(expectedTotal, total);
        logger.info(String.format("---- total recurring profiles [%s]", total));
    }

    private void checkInvoiceById(String invoiceId, String number, int expectedLinesTotal) throws Exception {
        logger.info("-- check lines of invoice");
        final Json response = test.executeFunction( "findInvoiceById", Json.map().set("invoice_id", invoiceId));
        checkResponse(response);
        assertTrue(response.json("response").contains("invoice"));
        assertTrue(response.json("response").object("invoice") instanceof Json);

        final Json invoice = response.json("response").json("invoice");
        assertEquals(number, invoice.string("number"));
        assertEquals(expectedLinesTotal, invoice.json("lines").jsons("line").size());
    }

    private void checkRecurringById(String recurringId, int expectedLinesTotal) throws Exception {
        logger.info("-- check lines of recurring");
        final Json response = test.executeFunction( "findRecurringById", Json.map().set("recurring_id", recurringId));
        checkResponse(response);
        assertTrue(response.json("response").contains("recurring"));
        assertTrue(response.json("response").object("recurring") instanceof Json);

        final Json recurring = response.json("response").json("recurring");
        assertEquals(expectedLinesTotal, recurring.json("lines").jsons("line").size());
    }

    private void checkDeletedClient(String id) throws Exception {
        final Json response = test.executeFunction( "findClientById", Json.map().set("client_id", id), true);
        checkFailedResponse(response);
    }

    private void checkClientName(String id, String email, String firstName, String lastName) throws Exception {
        final Json response = test.executeFunction( "findClientById", Json.map().set("client_id", id));
        checkResponse(response);
        assertTrue(response.json("response").contains("client"));
        assertTrue(response.json("response").object("client") instanceof Json);

        final Json client = response.json("response").json("client");
        assertEquals(email, client.string("email"));
        assertEquals(firstName, client.string("first_name"));
        assertEquals(lastName, client.string("last_name"));
    }

    private Json findInvoicesByClient(String id) throws Exception {
        final Json response = test.executeFunction( "findInvoices", Json.map().set("client_id", id));
        return checkFindInvoiceResponse(response);
    }

    private Json findRecurringByClient(String id) throws Exception {
        final Json response = test.executeFunction( "findRecurring", Json.map().set("client_id", id));
        return checkFindRecurringResponse(response);
    }

    private Json findAll() throws Exception {
        final Json response = test.executeFunction( "findClients", Json.map());
        return checkFindResponse(response);
    }

    private Json findByEmail(String email) throws Exception {
        final Json response = test.executeFunction( "findClients", Json.map().set("email", email));
        return checkFindResponse(response);
    }

    private Json checkFindInvoiceResponse(Json response) {
        return checkFindResponse(response, "invoices", "invoice");
    }

    private Json checkFindRecurringResponse(Json response) {
        return checkFindResponse(response, "recurrings", "recurring");
    }

    private Json checkFindResponse(Json response) {
        return checkFindResponse(response, "clients", "client");
    }

    private Json checkFindResponse(Json response, String collection, String list) {
        checkResponse(response);
        assertTrue(response.json("response").contains(collection));

        final Json clients = response.json("response").json(collection);
        final int total = clients.integer("@total");
        if(total>0){
            assertTrue(clients.object(list) instanceof Json);
            if(total > 1){
                assertEquals(total, clients.jsons(list).size());
            }
        } else {
            assertTrue(!clients.contains(list) || clients.jsons(list).isEmpty());
        }

        return clients;
    }

    private void checkResponse(Json response) {
        assertNotNull(response);
        logger.info("--- " + response);
        assertTrue(!response.contains(Parameter.EXCEPTION_FLAG) || !response.bool(Parameter.EXCEPTION_FLAG));
        assertTrue(response.object("response") instanceof Json);
        assertEquals("ok", response.json("response").string("@status"));
    }

    private void checkFailedResponse(Json response) {
        assertNotNull(response);
        logger.info("--- " + response);
        assertTrue(response.bool(Parameter.EXCEPTION_FLAG));
        assertNotNull(response.json(Parameter.EXCEPTION_ADDITIONAL_INFO));
        assertEquals("fail", response.json(Parameter.EXCEPTION_ADDITIONAL_INFO).string("@status"));
        logger.info("--- Exception: " + response.string(Parameter.EXCEPTION_MESSAGE));
    }
}
