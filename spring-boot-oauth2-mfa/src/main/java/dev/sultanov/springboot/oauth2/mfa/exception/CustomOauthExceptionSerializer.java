package dev.sultanov.springboot.oauth2.mfa.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    private static final long serialVersionUID = 732811184355689914L;

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("errorCode", value.getErrorId());
        jsonGenerator.writeStringField("message", value.getErrorMsg());
        jsonGenerator.writeObjectField("params", null);
        jsonGenerator.writeStringField("type", "AUTH_ERROR");
        jsonGenerator.writeEndObject();
    }
}