package wf.garnier.oauth2clienttest;


import java.util.Random;

import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;

class RandomAuthorizedClientProvider implements OAuth2AuthorizedClientProvider {


    private final Random r = new Random();

    public RandomAuthorizedClientProvider() {
    }

    @Override
    public OAuth2AuthorizedClient authorize(OAuth2AuthorizationContext context) {
        if (r.nextInt(100) < 10) {
            System.out.println("💥💥💥💥💥💥💥💥 BOOM");
            throw new ClientAuthorizationRequiredException(context.getClientRegistration().getRegistrationId());
        } else {
            System.out.println("👍👍👍👍👍👍👍👍 TOKEN IS OK");
        }
        return null;
    }
}
