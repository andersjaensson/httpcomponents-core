/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.message;


import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.Header;
import org.apache.http.util.CharArrayBuffer;


/**
 * Interface for formatting elements of the HEAD section of an HTTP message.
 * This is the complement to {@link LineParser}.
 * There are individual methods for formatting a request line, a
 * status line, or a header line. The formatting does <i>not</i> include the
 * trailing line break sequence CR-LF.
 * Instances of this interface are expected to be stateless and thread-safe.
 *
 * <p>
 * The formatted lines are returned in memory, the formatter does not depend
 * on any specific IO mechanism.
 * In order to avoid unnecessary creation of temporary objects,
 * a buffer can be passed as argument to all formatting methods.
 * The implementation may or may not actually use that buffer for formatting.
 * If it is used, the buffer will first be cleared.
 * The argument buffer can always be re-used after the call. The buffer
 * returned as the result, if it is different from the argument buffer,
 * MUST NOT be modified.
 * </p>
 *
 *
 * @author <a href="mailto:rolandw AT apache.org">Roland Weber</a>
 *
 *
 * <!-- empty lines above to avoid 'svn diff' context problems -->
 * @version $Revision$ $Date$
 *
 * @since 4.0
 */
public interface LineFormatter {


    /**
     * Formats a request line.
     *
     * @param reqline   the request line to format
     * @param buffer    a buffer available for formatting, or
     *                  <code>null</code>.
     *                  The buffer will be cleared before use.
     *
     * @return  the formatted request line
     */
    CharArrayBuffer formatRequestLine(RequestLine reqline,
                                      CharArrayBuffer buffer) 
        ;


    /**
     * Formats a status line.
     *
     * @param statline  the status line to format
     * @param buffer    a buffer available for formatting, or
     *                  <code>null</code>.
     *                  The buffer will be cleared before use.
     * @param buffer    a buffer holding the line to parse
     *
     * @return  the formatted status line
     *
     * @throws ParseException        in case of a parse error
     */
    CharArrayBuffer formatStatusLine(StatusLine statline,
                                     CharArrayBuffer buffer) 
        ;


    /**
     * Formats a header.
     * Due to header continuation, the result may be multiple lines.
     * In order to generate well-formed HTTP, the lines in the result
     * must be separated by the HTTP line break sequence CR-LF.
     * There is <i>no</i> trailing CR-LF in the result.
     * <br/>
     * See the class comment for details about the buffer argument.
     *
     * @param header    the header to format
     * @param buffer    a buffer available for formatting, or
     *                  <code>null</code>.
     *                  The buffer will be cleared before use.
     *
     * @return  a buffer holding the formatted header, never <code>null</code>.
     *          The returned buffer may be different from the argument buffer.
     *
     * @throws ParseException        in case of a parse error
     */
    CharArrayBuffer formatHeader(Header header, CharArrayBuffer buffer)
        ;

}
