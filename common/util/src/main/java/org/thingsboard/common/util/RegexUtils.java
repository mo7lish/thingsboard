/**
 * Copyright © 2016-2025 The Thingsboard Authors
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
package org.thingsboard.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.SOFT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
    /**
 * Utility class that provides helper methods for working with regular expressions.
 * <p>
 * It supports:
 * <ul>
 *   <li>Predefined patterns such as UUID format</li>
 *   <li>Convenience methods for replacing parts of a string using a Pattern or a regex</li>
 *   <li>Caching compiled regular expressions to avoid recompilation overhead</li>
 *   <li>Helper methods to check matches and safely extract capturing groups</li>
 * </ul>
 * This centralises common regex logic and makes it easier to reuse and maintain.
 */
public class RegexUtils {

    public static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    private static final ConcurrentMap<String, Pattern> patternsCache = new ConcurrentReferenceHashMap<>(16, SOFT);
/**
 * Replaces all substrings in the given input that match the provided {@link Pattern}.
 *
 * @param s        the original input string
 * @param pattern  the compiled regular expression pattern to apply
 * @param replacer a function that receives each matched substring and returns the replacement
 * @return a new string with all matches replaced using the given replacer
 */
    public static String replace(String s, Pattern pattern, UnaryOperator<String> replacer) {
        return pattern.matcher(s).replaceAll(matchResult -> {
            return replacer.apply(matchResult.group());
        });
    }
/**
 * Replaces all substrings in the given input that match the provided regular expression.
 * <p>
 * The string pattern is compiled and cached internally to avoid recompiling
 * the same regex on subsequent calls.
 *
 * @param input    the original input string
 * @param pattern  the regular expression to apply
 * @param replacer a function that receives the match result and returns the replacement
 * @return a new string with all matches replaced using the given replacer
 */
    public static String replace(String input, @Language("regexp") String pattern, Function<MatchResult, String> replacer) {
        return patternsCache.computeIfAbsent(pattern, Pattern::compile).matcher(input).replaceAll(replacer);
    }
/**
 * Checks whether the entire input string matches the given pattern.
 *
 * @param input   the string to test
 * @param pattern the compiled regular expression pattern
 * @return {@code true} if the whole input matches the pattern, {@code false} otherwise
 */
    public static boolean matches(String input, Pattern pattern) {
        return pattern.matcher(input).matches();
    }

    public static boolean matches(String input, @Language("regexp") String pattern) {
    return getCachedPattern(pattern).matcher(input).matches();
}

/**
 * Returns the value of a specific capturing group for the first match of the pattern.
 * <p>
 * If there is no match or the requested group is invalid, this method returns {@code null}
 * instead of throwing an exception.
 *
 * @param input   the string to search
 * @param pattern the compiled regular expression pattern
 * @param group   the capturing group index to return
 * @return the matched group value, or {@code null} if no valid match/group is found
 */
    public static String getMatch(String input, Pattern pattern, int group) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            try {
                return matcher.group(group);
            } catch (Exception ignored) {}
        }
        return null;
    }

}
