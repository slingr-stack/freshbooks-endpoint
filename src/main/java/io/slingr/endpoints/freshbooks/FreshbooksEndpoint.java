package io.slingr.endpoints.freshbooks;

import io.slingr.endpoints.HttpEndpoint;
import io.slingr.endpoints.exceptions.EndpointException;
import io.slingr.endpoints.exceptions.ErrorCode;
import io.slingr.endpoints.framework.annotations.EndpointFunction;
import io.slingr.endpoints.framework.annotations.EndpointProperty;
import io.slingr.endpoints.framework.annotations.EndpointWebService;
import io.slingr.endpoints.framework.annotations.SlingrEndpoint;
import io.slingr.endpoints.services.HttpService;
import io.slingr.endpoints.services.IHttpExceptionConverter;
import io.slingr.endpoints.services.exchange.Parameter;
import io.slingr.endpoints.utils.converters.ContentTypeFormat;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.ws.exchange.FunctionRequest;
import io.slingr.endpoints.ws.exchange.WebServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Freshbooks endpoint
 *
 * <p>Created by lefunes on 10/01/15.
 */
@SlingrEndpoint(name = "freshbooks")
public class FreshbooksEndpoint extends HttpEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(FreshbooksEndpoint.class);

    private static final String FRESHBOOKS_URL_PATTERN = "https://%s.freshbooks.com/api/2.1/xml-in";

    @EndpointProperty
    private String account;

    @EndpointProperty
    private String token;

    @Override
    public String getApiUri() {
        return String.format(FRESHBOOKS_URL_PATTERN, account);
    }

    @Override
    public void endpointStarted() {

        httpService().setDefaultEmptyPath("");
        httpService().setAllowExternalUrl(false);

        httpService().setupDefaultHeader(Parameter.CONTENT_TYPE, ContentTypeFormat.XML.getMimeType());
        httpService().setupDefaultHeader("User-Agent", String.format("slingr-%s-%s", properties().getApplicationName(), properties().getEndpointName()));

        httpService().setupBasicAuthentication(token, "x");
        httpService().setupExceptionConverter(new FreshbooksExceptionHandler());

        logger.info("Configured FreshBooks endpoint");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Generic HTTP functions
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get request
     */
    @EndpointFunction
    public Json get(FunctionRequest request){
        final Json response = defaultGetRequest(request);
        checkFailedResponses(response);
        return response;
    }

    /**
     * Post request
     */
    @EndpointFunction
    public Json post(FunctionRequest request){
        final Json response = defaultPostRequest(request);
        checkFailedResponses(response);
        return response;
    }

    /**
     * Put request
     */
    @EndpointFunction
    public Json put(FunctionRequest request){
        final Json response = defaultPutRequest(request);
        checkFailedResponses(response);
        return response;
    }

    /**
     * Head request
     */
    @EndpointFunction
    public Json head(FunctionRequest request){
        final Json response = defaultHeadRequest(request);
        checkFailedResponses(response);
        return response;
    }

    /**
     * Delete request
     */
    @EndpointFunction
    public Json delete(FunctionRequest request){
        final Json response = defaultDeleteRequest(request);
        checkFailedResponses(response);
        return response;
    }

    /**
     * Patch request
     */
    @EndpointFunction
    public Json patch(FunctionRequest request){
        final Json response = defaultPatchRequest(request);
        checkFailedResponses(response);
        return response;
    }

    /**
     * Options request
     */
    @EndpointFunction
    public Json options(FunctionRequest request){
        final Json response = defaultOptionsRequest(request);
        checkFailedResponses(response);
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Clients
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Create client request
     */
    @EndpointFunction
    public Json createClient(Json request){
        final Json client = Json.map().set("client", request);
        final Json response = executeRequest("client.create", client, "Create client", "Client created");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Update client request
     */
    @EndpointFunction
    public Json updateClient(Json request){
        final Json client = Json.map().set("client", request);
        final Json response = executeRequest("client.update", client, "Update client", "Client updated");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Remove client request
     */
    @EndpointFunction
    public Json removeClient(Json request){
        final Json response = executeRequest("client.delete", request, "Remove client", "Client removed");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Find client by id request
     */
    @EndpointFunction
    public Json findClientById(Json request){
        final Json response = executeRequest("client.get", request, "Find client by id", "Found client");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Find clients request
     */
    @EndpointFunction
    public Json findClients(Json request){
        final Json response = executeRequest("client.list", request, "Find clients", "Found clients");
        checkFailedResponses(response);
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Invoices
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Create invoice request
     */
    @EndpointFunction
    public Json createInvoice(Json request){
        final Json invoice = Json.map().set("invoice", request);
        final Json response = executeRequest("invoice.create", invoice, "Create invoice", "Invoice created");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Update invoice request
     */
    @EndpointFunction
    public Json updateInvoice(Json request){
        final Json invoice = Json.map().set("invoice", request);
        final Json response = executeRequest("invoice.update", invoice, "Update invoice", "Invoice updated");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Remove invoice request
     */
    @EndpointFunction
    public Json removeInvoice(Json request){
        final Json response = executeRequest("invoice.delete", request, "Remove invoice", "Invoice removed");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Find invoice by id request
     */
    @EndpointFunction
    public Json findInvoiceById(Json request){
        final Json response = executeRequest("invoice.get", request, "Find invoice by id", "Found invoice");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Find invoices request
     */
    @EndpointFunction
    public Json findInvoices(Json request){
        final Json response = executeRequest("invoice.list", request, "Find invoices", "Found invoices");
        checkFailedResponses(response);
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Recurring
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Create recurring profile request
     */
    @EndpointFunction
    public Json createRecurring(Json request){
        final Json recurring = Json.map().set("recurring", request);
        final Json response = executeRequest("recurring.create", recurring, "Create recurring profile", "Recurring profile created");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Update recurring profile request
     */
    @EndpointFunction
    public Json updateRecurring(Json request){
        final Json recurring = Json.map().set("recurring", request);
        final Json response = executeRequest("recurring.update", recurring, "Update recurring profile", "Recurring profile updated");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Remove recurring profile request
     */
    @EndpointFunction
    public Json removeRecurring(Json request){
        final Json response = executeRequest("recurring.delete", request, "Remove recurring profile", "Recurring profile removed");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Find recurring profile by id request
     */
    @EndpointFunction
    public Json findRecurringById(Json request){
        final Json response = executeRequest("recurring.get", request, "Find recurring profile by id", "Found recurring profile");
        checkFailedResponses(response);
        return response;
    }

    /**
     * Find recurring profiles request
     */
    @EndpointFunction
    public Json findRecurring(Json request){
        final Json response = executeRequest("recurring.list", request, "Find recurring profiles", "Found recurring profiles");
        checkFailedResponses(response);
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Currencies
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find currencies request
     */
    @EndpointFunction
    public Json findCurrencies(Json request){
        final Json response = executeRequest("currency.list", null, "Find currencies", "Found currencies");
        checkFailedResponses(response);
        return response;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * All the webhooks are disabled
     */
    @EndpointWebService
    public WebServiceResponse webhooksAreDisabled(){
        return HttpService.defaultWebhookResponse("Invalid request", 404);
    }

    /**
     * Executes the request on the Freshbooks services
     *
     * @param method name of the freshbooks method
     * @param requestBody body of the request
     * @param iniLogMessage message to show before to execute request
     * @param endLogMessage message to show after receive the response
     * @return response from freshbooks
     * @throws EndpointException exception if something is failing
     */
    private Json executeRequest(String method, Json requestBody, String iniLogMessage, String endLogMessage) throws EndpointException {
        Json request = Json.map().setIfNotEmpty("@method", method);
        if(requestBody!= null) {
            request.merge(requestBody);
        }

        request =  Json.map().set("body", Json.map().set("request", request));

        logger.info(String.format("%s [%s]", iniLogMessage, request));

        final Json response = httpService().post(request);

        logger.info(String.format("%s [%s]", endLogMessage, response));
        return response;
    }

    /**
     * Converts the Freshbooks errors on endpoint exceptions
     *
     * @param body response from freshbooks to check
     * @throws EndpointException exception equivalent to the freshbooks error
     */
    private static void checkFailedResponses(Json body) throws EndpointException {
        if(body != null && body.contains("response") && body.json("response").contains("@status")){
            if("fail".equalsIgnoreCase(body.json("response").string("@status"))){
                final Json error = body.json("response");

                final StringBuilder message = new StringBuilder();
                message.append("FreshBooks error ");
                if(body.json("response").contains("error")){
                    message.append("[").append(body.json("response").string("error")).append("] ");
                }

                throw EndpointException.permanent(ErrorCode.API, message.toString(), error).returnCode(400);
            }
        }
    }

    /**
     * Parse the Freshbooks exceptions
     */
    private class FreshbooksExceptionHandler implements IHttpExceptionConverter {
        @Override
        public EndpointException convertToEndpointException(Exception exception) {
            return EndpointException.parseHTTPExceptions(exception, "error", "referral_link");
        }
    }
}
