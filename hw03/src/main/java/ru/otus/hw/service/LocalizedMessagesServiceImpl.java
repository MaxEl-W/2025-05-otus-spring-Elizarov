package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.LocaleConfig;

@RequiredArgsConstructor
@Service
public class LocalizedMessagesServiceImpl implements LocalizedMessagesService {

    private final LocaleConfig localeConfig;

    private final MessageSource messageSource;

    // Доделать
    @Override
    public String getMessage(String code, Object... args) {
        var locale = localeConfig.getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}
