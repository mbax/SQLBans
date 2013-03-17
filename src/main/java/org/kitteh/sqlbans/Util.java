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

public final class Util {

    public static boolean isIP(String string) {
        final String[] split = string.split("\\.");
        if (split.length == 4) {
            for (final String s : split) {
                try {
                    final int i = Integer.parseInt(s);
                    if ((i < 0) || (i > 256) || !String.valueOf(i).equals(s)) {
                        return false;
                    }
                } catch (final Exception e) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void nullCheck(Object check, String name) {
        if (check == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
    }

    public static String separatistsUnite(String[] args, String separator) {
        return Util.separatistsUnite(args, separator, 0, args.length - 1);
    }

    public static String separatistsUnite(String[] args, String separator, int start) {
        return Util.separatistsUnite(args, separator, start, args.length - 1);
    }

    public static String separatistsUnite(String[] args, String separator, int start, int end) {
        final StringBuilder builder = new StringBuilder();
        if (start < 0) {
            start = 0;
        }
        if ((start > end) || (start >= args.length)) {
            return "";
        }
        builder.append(args[start]);
        for (int x = start + 1; (x <= end) && (x < args.length); x++) {
            builder.append(separator).append(args[x]);
        }
        return builder.toString();
    }
}