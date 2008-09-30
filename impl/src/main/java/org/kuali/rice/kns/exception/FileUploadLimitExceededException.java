/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.exception;

/**
 * This class represents an FileUploadLimitExceededException.
 * 
 * 
 */

public class FileUploadLimitExceededException extends KualiException {

    /**
     * Create an FileUploadLimitExceededException with the given message
     * 
     * @param message
     */
    public FileUploadLimitExceededException(String message) {
        super(message);
    }

    /**
     * Create an FileUploadLimitExceededException with the given message and cause
     * 
     * @param message
     * @param cause
     */
    public FileUploadLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

}
