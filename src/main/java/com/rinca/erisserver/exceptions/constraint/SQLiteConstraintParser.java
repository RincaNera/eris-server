package com.rinca.erisserver.exceptions.constraint;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(name = "app.db-parser", havingValue = "sqlite")
public class SQLiteConstraintParser implements ConstraintParserStrategy {

    @Override
    public String parse(String errorMessage) {
        Pattern pattern = Pattern.compile("UNIQUE constraint failed: (\\w+)\\.(\\w+)");
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            String table = matcher.group(1);
            String field = matcher.group(2);
            return getUserMessage(table, field);
        }
        throw new RuntimeException();
    }
}
