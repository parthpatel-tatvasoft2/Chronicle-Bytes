/*
 * Copyright 2014 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.bytes;

/**
 * @author peter.lawrey
 */
public enum StopCharTesters implements StopCharTester {
    COMMA_STOP {
        @Override
        public boolean isStopChar(int ch) {
            return ch < ' ' || ch == ',';
        }
    }, CONTROL_STOP {
        @Override
        public boolean isStopChar(int ch) {
            return ch < ' ';
        }
    }, NEW_LINE_STOP {
        @Override
        public boolean isStopChar(int ch) {
            return ch == '\n';
        }
    },
    SPACE_STOP {
        @Override
        public boolean isStopChar(int ch) {
            return Character.isWhitespace(ch) || ch == 0;
        }
    },
    XML_TEXT {
        @Override
        public boolean isStopChar(int ch) {
            return ch == '"' || ch == '<' || ch == '>' || ch == 0;
        }
    },
    FIX_TEXT {
        @Override
        public boolean isStopChar(int ch) {
            return ch <= 1;
        }
    },
    QUOTES {
        @Override
        public boolean isStopChar(int ch) throws IllegalStateException {
            return ch == '"' || ch <= 0;
        }
    },
    ALL {
        @Override
        public boolean isStopChar(int ch) {
            return ch < 0;
        }
    };

    public static StopCharTester forChars(CharSequence sequence) {
        if (sequence.length() == 1)
            return forChar(sequence.charAt(0));
        return new CSCSTester(sequence);
    }

    private static StopCharTester forChar(char ch) {
        return new CharCSTester(ch);
    }

    static class CSCSTester implements StopCharTester {
        private final String seperators;

        public CSCSTester(CharSequence cs) {
            seperators = cs.toString();
        }

        @Override
        public boolean isStopChar(int ch) {
            return seperators.indexOf(ch) >= 0;
        }
    }

    static class CharCSTester implements StopCharTester {
        private final char ch;

        public CharCSTester(char ch) {
            this.ch = ch;
        }

        @Override
        public boolean isStopChar(int ch) {
            return this.ch == ch;
        }
    }
}
