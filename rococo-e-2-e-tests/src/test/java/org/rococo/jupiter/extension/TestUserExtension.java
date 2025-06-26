package org.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.rococo.api.grpc.UserdataGrpcClient;
import org.rococo.api.rest.AuthApiClient;
import org.rococo.jupiter.annotation.ApiLogin;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.model.TestData;
import org.rococo.model.UserJson;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.utils.RandomDataUtils.randomUsername;

@ParametersAreNonnullByDefault
public class TestUserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(TestUserExtension.class);
    private static final String DEFAULT_PW = "12345";

    private final AuthApiClient authClient = new AuthApiClient();
    private final UserdataGrpcClient userdataClient = new UserdataGrpcClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        TestUser testUserAnnotation = context.getRequiredTestMethod().getAnnotation(TestUser.class);
        ApiLogin apiLoginAnnotation = context.getRequiredTestMethod().getAnnotation(ApiLogin.class);

        if (apiLoginAnnotation != null) {
            final String username = "".equals(apiLoginAnnotation.testUser().username()) ?
                    randomUsername() : apiLoginAnnotation.testUser().username();

            authClient.createUser(username, DEFAULT_PW);
            UserJson created = userdataClient.getCurrent(username);
            setUser(created.addTestData(new TestData(DEFAULT_PW)));

        } else if (testUserAnnotation != null) {

            final String username = "".equals(testUserAnnotation.username()) ?
                    randomUsername() : testUserAnnotation.username();
            authClient.createUser(username, DEFAULT_PW);
            UserJson created = userdataClient.getCurrent(username);
            setUser(created.addTestData(new TestData(DEFAULT_PW)));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

    @Nullable
    public static UserJson createdUser() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                UserJson.class
        );
    }

    public static void setUser(UserJson testUser) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                testUser
        );
    }
}
