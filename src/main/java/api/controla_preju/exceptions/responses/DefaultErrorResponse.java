package api.controla_preju.exceptions.responses;

import java.time.Instant;

public record DefaultErrorResponse(Instant timestamp,
                                   Integer status,
                                   String message,
                                   String path) {
}
