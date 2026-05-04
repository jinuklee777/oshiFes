package com.oshifes.domain.auth.api;

import com.oshifes.domain.auth.api.CsrfController.CsrfResponse;
import com.oshifes.global.common.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import static org.assertj.core.api.Assertions.assertThat;

class CsrfControllerTest {

    @Test
    void csrf_returnsTokenPayload() {
        CsrfController controller = new CsrfController();
        DefaultCsrfToken csrfToken = new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", "token-value");

        ApiResponse<CsrfResponse> response = controller.csrf(csrfToken);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getHeaderName()).isEqualTo("X-XSRF-TOKEN");
        assertThat(response.getData().getParameterName()).isEqualTo("_csrf");
        assertThat(response.getData().getToken()).isEqualTo("token-value");
    }
}
