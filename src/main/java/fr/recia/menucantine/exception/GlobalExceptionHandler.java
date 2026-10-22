/**
 * Copyright (C) 2024 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2024 GIP-RECIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.menucantine.exception;

import fr.recia.menucantine.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String UNEXPECTED_ERROR_MESSAGE =
            "Une erreur est survenue. Les menus de la cantine sont indisponibles pour le moment";

    @ExceptionHandler(CustomMenuCantineException.class)
    public ResponseEntity<ResponseExceptionData> handleCustomMenuCantineException(CustomMenuCantineException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(
                new ResponseExceptionData(ex.getErrorCode(), ex.getDisplayMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseExceptionData> handleUnexpectedException(RuntimeException ex, HttpServletRequest request) {
        log.error("Une erreur innatendue s'est produite : {}. UAI : {}, Date : {}",
                ex.getMessage(), request.getParameter("uai"), request.getParameter("dateJour"), ex);
        return new ResponseEntity<>(
                new ResponseExceptionData(ErrorCode.UNEXPECTED_ERROR, UNEXPECTED_ERROR_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}