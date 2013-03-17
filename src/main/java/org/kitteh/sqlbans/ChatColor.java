/*
 * SQLBans
 * Copyright 2012 Matt Baxter
 *
 * Google Gson
 * Copyright 2008-2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kitteh.sqlbans;

/**
 * Chat colors
 */
public enum ChatColor {

    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r');

    public static final char COLOR_CHAR = '\u00A7';

    public static String translateChar(char character, String string) {
        final char[] array = string.toCharArray();
        for (int x = 0; x < (array.length - 1); x++) {
            if ((array[x] == character) && ("0123456789abcdefklmnorABCDEFKLMNOR".indexOf(array[x + 1]) != -1)) {
                array[x] = ChatColor.COLOR_CHAR;
                array[x + 1] = Character.toLowerCase(array[x + 1]);
            }
        }
        return new String(array);
    }

    private final String toString;

    private ChatColor(char code) {
        this.toString = new String(new char[] { ChatColor.COLOR_CHAR, code });
    }

    @Override
    public String toString() {
        return this.toString;
    }
}