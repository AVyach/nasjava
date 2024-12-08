package nas.nas.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nas.nas.exception.ExternalServiceError;
import nas.nas.model.TokenPayload;
import sso.CheckRequest;
import sso.CheckResponse;
import sso.LogoutRequest;
import sso.LogoutResponse;
import sso.SSOGrpc;
import sso.SignInRequest;
import sso.SignInResponse;
import sso.SignUpRequest;
import sso.SignUpResponse;

public class AuthService {
    private SSOGrpc.SSOBlockingStub authServer;

    public AuthService() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 4001)
            .usePlaintext()
            .build();
    
        authServer = SSOGrpc.newBlockingStub(channel);
    }

    public TokenPayload check(final String token) throws ExternalServiceError {
        CheckResponse resp = this.authServer.check(CheckRequest.newBuilder()
            .setToken(token)
            .build());
        
        if (resp.getRespStatus().getStatus() == 0) {
            return new TokenPayload(resp.getIsValidSession(), resp.getPayload().getUuid(), resp.getPayload().getUsername());
        } else {
            throw new ExternalServiceError(String.format("%d", resp.getRespStatus().getStatus()));
        }
    }

    public String signUp(final String userName, final String password) throws ExternalServiceError {
        SignUpResponse resp = this.authServer.signUp(SignUpRequest.newBuilder()
            .setUsername(userName)
            .setPassword(password)
            .build());
        
        if (resp.getRespStatus().getStatus() == 0) {
            return resp.getToken();
        } else {
            throw new ExternalServiceError(String.format("%d", resp.getRespStatus().getStatus()));
        }
    }

    public String signIn(final String userName, final String password) throws ExternalServiceError {
        SignInResponse resp = this.authServer.signIn(SignInRequest.newBuilder()
            .setUsername(userName)
            .setPassword(password)
            .build());

        if (resp.getRespStatus().getStatus() == 0) {
            return resp.getToken();
        } else {
            throw new ExternalServiceError(String.format("%d", resp.getRespStatus().getStatus()));
        }
    }

    public void logout(final String token) throws ExternalServiceError {
        LogoutResponse resp = this.authServer.logout(LogoutRequest.newBuilder()
            .setToken(token)
            .build());

        if (resp.getRespStatus().getStatus() != 0) {
            throw new ExternalServiceError(String.format("%d", resp.getRespStatus().getStatus()));
        }
    }

    public void logoutAll(final String token) throws ExternalServiceError {
        LogoutResponse resp = this.authServer.logoutAll(LogoutRequest.newBuilder()
            .setToken(token)
            .build());

        if (resp.getRespStatus().getStatus() != 0) {
            throw new ExternalServiceError(String.format("%d", resp.getRespStatus().getStatus()));
        }
    }
}
