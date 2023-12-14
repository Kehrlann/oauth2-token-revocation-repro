package wf.garnier.oauth2clienttest;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
class MessagesController {


    private final OAuth2AuthorizedClientManager clientManager;

    public MessagesController(OAuth2AuthorizedClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @GetMapping(value = "/")
    public List<Message> foo(@RegisteredOAuth2AuthorizedClient("messaging-client-oidc") OAuth2AuthorizedClient authorizedClient) {
        var client = RestClient.create();
        var typeRef = new ParameterizedTypeReference<List<Message>>() {
        };
        return client.get()
                .uri("http://127.0.0.1:8090/messages")
                .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                .retrieve()
                .body(typeRef);
    }

    @GetMapping(value = "/manager")
    public List<Message> withClientManager(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        if (authentication == null) {
            authentication = new AnonymousAuthenticationToken("anonymous",
                    "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        }
        // @formatter:off
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("messaging-client-oidc")
                .principal(authentication)
                .attribute(HttpServletRequest.class.getName(), request)
                .attribute(HttpServletResponse.class.getName(), response)
                .build();
        // @formatter:on

        var authorizedClient = clientManager.authorize(authorizeRequest);
        var client = RestClient.create();
        var typeRef = new ParameterizedTypeReference<List<Message>>() {
        };
        return client.get()
                .uri("http://127.0.0.1:8090/messages")
                .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                .retrieve()
                .body(typeRef);
    }

    record Message(String content) {
    }


}
