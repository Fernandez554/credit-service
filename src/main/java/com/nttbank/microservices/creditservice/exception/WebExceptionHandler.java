package com.nttbank.microservices.creditservice.exception;

import com.nttbank.microservices.creditservice.utils.Constants;
import feign.FeignException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * A custom exception handler for handling various types of errors in a reactive Spring WebFlux
 * application. It extends the {@link AbstractErrorWebExceptionHandler} to provide custom error
 * handling logic for validation errors, illegal arguments, and other exceptions, formatting the
 * errors in a consistent response format.
 */
@Component
@Order(-1)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

  public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
      ApplicationContext applicationContext, ServerCodecConfigurer configure) {
    super(errorAttributes, resources, applicationContext);
    this.setMessageWriters(configure.getWriters());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(),
        this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Throwable error = getError(request);

    if (error instanceof WebExchangeBindException bindException) {
      return handleValidationErrors(bindException);
    }

    if (error instanceof IllegalArgumentException || error instanceof IllegalStateException) {
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put(Constants.ERROR_KEY, "Invalid request");
      errorDetails.put(Constants.MESSAGE_KEY, error.getMessage());
      errorDetails.put(Constants.PATH_KEY, request.path());

      return ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
          .bodyValue(errorDetails);
    }

    if (error instanceof ResponseStatusException responseStatusException) {
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put(Constants.ERROR_KEY, "Invalid request");
      errorDetails.put(Constants.MESSAGE_KEY, responseStatusException.getReason());
      errorDetails.put(Constants.PATH_KEY, request.path());
      return ServerResponse.status(responseStatusException.getStatusCode())
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(errorDetails);
    }

    if (error instanceof FeignException.NotFound) {
      Map<String, Object> errorAttributes = new HashMap<>();
      errorAttributes.put(Constants.STATUS_KEY, HttpStatus.NOT_FOUND.value());
      errorAttributes.put(Constants.ERROR_KEY, "Resource Not Found");
      errorAttributes.put(Constants.MESSAGE_KEY, "Customer not found.");
      return ServerResponse.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
          .bodyValue(errorAttributes);
    }

    Map<String, Object> errorAttributes = getErrorAttributes(request,
        ErrorAttributeOptions.defaults());
    int statusCode = (int) errorAttributes.getOrDefault(Constants.STATUS_KEY, 500);

    return ServerResponse.status(statusCode).contentType(MediaType.APPLICATION_JSON)
        .bodyValue(errorAttributes);

  }

  private Mono<ServerResponse> handleValidationErrors(WebExchangeBindException bindException) {
    List<String> errors = bindException.getFieldErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

    Map<String, Object> response = new HashMap<>();
    response.put(Constants.STATUS_KEY, 400);
    response.put(Constants.MESSAGE_KEY, "Invalid input data");
    response.put(Constants.ERROR_KEY, errors);

    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(response);
  }

}
