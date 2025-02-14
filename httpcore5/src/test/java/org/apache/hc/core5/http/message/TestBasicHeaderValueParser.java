/*
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
package org.apache.hc.core5.http.message;

import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.util.CharArrayBuffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for header value parsing.
 *
 * @version $Id$
 */
public class TestBasicHeaderValueParser {

    private BasicHeaderValueParser parser;

    @BeforeEach
    public void setup() {
        this.parser = BasicHeaderValueParser.INSTANCE;
    }

    @Test
    public void testParseHeaderElements() throws Exception {
        final String headerValue = "name1 = value1; name2; name3=\"value3\" , name4=value4; " +
            "name5=value5, name6= ; name7 = value7; name8 = \" value8\"";
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(headerValue);
        final ParserCursor cursor = new ParserCursor(0, buf.length());
        final HeaderElement[] elements = this.parser.parseElements(buf, cursor);
        // there are 3 elements
        Assertions.assertEquals(3,elements.length);
        // 1st element
        Assertions.assertEquals("name1",elements[0].getName());
        Assertions.assertEquals("value1",elements[0].getValue());
        // 1st element has 2 getParameters()
        Assertions.assertEquals(2,elements[0].getParameters().length);
        Assertions.assertEquals("name2",elements[0].getParameters()[0].getName());
        Assertions.assertNull(elements[0].getParameters()[0].getValue());
        Assertions.assertEquals("name3",elements[0].getParameters()[1].getName());
        Assertions.assertEquals("value3",elements[0].getParameters()[1].getValue());
        // 2nd element
        Assertions.assertEquals("name4",elements[1].getName());
        Assertions.assertEquals("value4",elements[1].getValue());
        // 2nd element has 1 parameter
        Assertions.assertEquals(1,elements[1].getParameters().length);
        Assertions.assertEquals("name5",elements[1].getParameters()[0].getName());
        Assertions.assertEquals("value5",elements[1].getParameters()[0].getValue());
        // 3rd element
        Assertions.assertEquals("name6",elements[2].getName());
        Assertions.assertEquals("",elements[2].getValue());
        // 3rd element has 2 getParameters()
        Assertions.assertEquals(2,elements[2].getParameters().length);
        Assertions.assertEquals("name7",elements[2].getParameters()[0].getName());
        Assertions.assertEquals("value7",elements[2].getParameters()[0].getValue());
        Assertions.assertEquals("name8",elements[2].getParameters()[1].getName());
        Assertions.assertEquals(" value8",elements[2].getParameters()[1].getValue());
    }

    @Test
    public void testParseHEEscaped() {
        final String headerValue =
          "test1 =  \"\\\"stuff\\\"\", test2= \"\\\\\", test3 = \"stuff, stuff\"";
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(headerValue);
        final ParserCursor cursor = new ParserCursor(0, buf.length());
        final HeaderElement[] elements = this.parser.parseElements(buf, cursor);
        Assertions.assertEquals(3, elements.length);
        Assertions.assertEquals("test1", elements[0].getName());
        Assertions.assertEquals("\"stuff\"", elements[0].getValue());
        Assertions.assertEquals("test2", elements[1].getName());
        Assertions.assertEquals("\\", elements[1].getValue());
        Assertions.assertEquals("test3", elements[2].getName());
        Assertions.assertEquals("stuff, stuff", elements[2].getValue());
    }

    @Test
    public void testHEFringeCase1() throws Exception {
        final String headerValue = "name1 = value1,";
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(headerValue);
        final ParserCursor cursor = new ParserCursor(0, buf.length());
        final HeaderElement[] elements = this.parser.parseElements(buf, cursor);
        Assertions.assertEquals(1, elements.length, "Number of elements");
    }

    @Test
    public void testHEFringeCase2() throws Exception {
        final String headerValue = "name1 = value1, ";
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(headerValue);
        final ParserCursor cursor = new ParserCursor(0, buf.length());
        final HeaderElement[] elements = this.parser.parseElements(buf, cursor);
        Assertions.assertEquals(1, elements.length, "Number of elements");
    }

    @Test
    public void testHEFringeCase3() throws Exception {
        final String headerValue = ",, ,, ,";
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(headerValue);
        final ParserCursor cursor = new ParserCursor(0, buf.length());
        final HeaderElement[] elements = this.parser.parseElements(buf, cursor);
        Assertions.assertEquals(0, elements.length, "Number of elements");
    }

    @Test
    public void testNVParse() {

        String s = "test";
        CharArrayBuffer buffer = new CharArrayBuffer(64);
        buffer.append(s);
        ParserCursor cursor = new ParserCursor(0, s.length());

        NameValuePair param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertNull(param.getValue());
        Assertions.assertEquals(s.length(), cursor.getPos());
        Assertions.assertTrue(cursor.atEnd());

        s = "test;";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertNull(param.getValue());
        Assertions.assertEquals(s.length(), cursor.getPos());
        Assertions.assertTrue(cursor.atEnd());

        s = "test  ,12";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertNull(param.getValue());
        Assertions.assertEquals(s.length() - 2, cursor.getPos());
        Assertions.assertFalse(cursor.atEnd());

        s = "test=stuff";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertEquals("stuff", param.getValue());
        Assertions.assertEquals(s.length(), cursor.getPos());
        Assertions.assertTrue(cursor.atEnd());

        s = "   test  =   stuff ";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertEquals("stuff", param.getValue());
        Assertions.assertEquals(s.length(), cursor.getPos());
        Assertions.assertTrue(cursor.atEnd());

        s = "   test  =   stuff ;1234";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertEquals("stuff", param.getValue());
        Assertions.assertEquals(s.length() - 4, cursor.getPos());
        Assertions.assertFalse(cursor.atEnd());

        s = "test  = \"stuff\"";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertEquals("stuff", param.getValue());

        s = "test  = \"  stuff\\\"\"";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertEquals("  stuff\"", param.getValue());

        s = "  test";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("test", param.getName());
        Assertions.assertNull(param.getValue());

        s = "  ";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("", param.getName());
        Assertions.assertNull(param.getValue());

        s = " = stuff ";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        param = this.parser.parseNameValuePair(buffer, cursor);
        Assertions.assertEquals("", param.getName());
        Assertions.assertEquals("stuff", param.getValue());
    }

    @Test
    public void testNVParseAll() {
        String s =
            "test; test1 =  stuff   ; test2 =  \"stuff; stuff\"; test3=\"stuff";
        CharArrayBuffer buffer = new CharArrayBuffer(16);
        buffer.append(s);
        ParserCursor cursor = new ParserCursor(0, s.length());

        NameValuePair[] params = this.parser.parseParameters(buffer, cursor);
        Assertions.assertEquals("test", params[0].getName());
        Assertions.assertNull(params[0].getValue());
        Assertions.assertEquals("test1", params[1].getName());
        Assertions.assertEquals("stuff", params[1].getValue());
        Assertions.assertEquals("test2", params[2].getName());
        Assertions.assertEquals("stuff; stuff", params[2].getValue());
        Assertions.assertEquals("test3", params[3].getName());
        Assertions.assertEquals("stuff", params[3].getValue());
        Assertions.assertEquals(s.length(), cursor.getPos());
        Assertions.assertTrue(cursor.atEnd());

        s =
            "test; test1 =  stuff   ; test2 =  \"stuff; stuff\"; test3=\"stuff\",123";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());

        params = this.parser.parseParameters(buffer, cursor);
        Assertions.assertEquals("test", params[0].getName());
        Assertions.assertNull(params[0].getValue());
        Assertions.assertEquals("test1", params[1].getName());
        Assertions.assertEquals("stuff", params[1].getValue());
        Assertions.assertEquals("test2", params[2].getName());
        Assertions.assertEquals("stuff; stuff", params[2].getValue());
        Assertions.assertEquals("test3", params[3].getName());
        Assertions.assertEquals("stuff", params[3].getValue());
        Assertions.assertEquals(s.length() - 3, cursor.getPos());
        Assertions.assertFalse(cursor.atEnd());

        s = "  ";
        buffer = new CharArrayBuffer(16);
        buffer.append(s);
        cursor = new ParserCursor(0, s.length());
        params = this.parser.parseParameters(buffer, cursor);
        Assertions.assertEquals(0, params.length);
    }

    @Test
    public void testNVParseEscaped() {
        final String headerValue =
          "test1 =  \"\\\"stuff\\\"\"; test2= \"\\\\\"; test3 = \"stuff; stuff\"";
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(headerValue);
        final ParserCursor cursor = new ParserCursor(0, buf.length());
        final NameValuePair[] params = this.parser.parseParameters(buf, cursor);
        Assertions.assertEquals(3, params.length);
        Assertions.assertEquals("test1", params[0].getName());
        Assertions.assertEquals("\"stuff\"", params[0].getValue());
        Assertions.assertEquals("test2", params[1].getName());
        Assertions.assertEquals("\\", params[1].getValue());
        Assertions.assertEquals("test3", params[2].getName());
        Assertions.assertEquals("stuff; stuff", params[2].getValue());
    }

}
