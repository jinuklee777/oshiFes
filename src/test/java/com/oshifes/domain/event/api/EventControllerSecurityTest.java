package com.oshifes.domain.event.api;

import com.oshifes.domain.event.api.dto.EventRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.assertj.core.api.Assertions.assertThat;

class EventControllerSecurityTest {

    @Test
    void eventWriteApis_requireAdminRole() throws NoSuchMethodException {
        assertThat(preAuthorizeValue("createEvent", EventRequest.class))
                .isEqualTo("hasRole('ADMIN')");
        assertThat(preAuthorizeValue("updateEvent", Long.class, EventRequest.class))
                .isEqualTo("hasRole('ADMIN')");
        assertThat(preAuthorizeValue("deleteEvent", Long.class))
                .isEqualTo("hasRole('ADMIN')");
    }

    private String preAuthorizeValue(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        PreAuthorize preAuthorize = EventController.class
                .getDeclaredMethod(methodName, parameterTypes)
                .getAnnotation(PreAuthorize.class);

        assertThat(preAuthorize).isNotNull();
        return preAuthorize.value();
    }
}
