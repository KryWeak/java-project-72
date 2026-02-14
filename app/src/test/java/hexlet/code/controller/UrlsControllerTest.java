package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.NamedRoutes;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import io.javalin.validation.Validator;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.doReturn;

class UrlsControllerTest {

    private Context ctx;

    private static Validator<Long> longValidator(Long value) {
        @SuppressWarnings("unchecked")
        Validator<Long> v = mock(Validator.class);
        doReturn(value).when(v).get();                     // ← doReturn вместо when!
        return v;
    }

    private static Validator<String> stringValidator(String value) {
        @SuppressWarnings("unchecked")
        Validator<String> v = mock(Validator.class);
        doReturn(value != null ? value : "").when(v).getOrDefault(any());
        doReturn(value).when(v).get();
        return v;
    }

    @BeforeEach
    void setUp() {
        ctx = mock(Context.class);

        when(ctx.pathParamAsClass(anyString(), eq(Long.class))).thenAnswer(i -> longValidator(1L));
        when(ctx.formParamAsClass(eq("url"), eq(String.class))).thenAnswer(i -> stringValidator("https://example.com"));
    }

    @Test
    void testRoot() {
        when(ctx.consumeSessionAttribute("flash")).thenReturn("some flash");
        UrlsController.root(ctx);
        verify(ctx).render(eq("index.jte"), argThat(map -> {
            BasePage page = (BasePage) map.get("page");
            return page != null && "some flash".equals(page.getFlash());
        }));
    }

    @Test
    void testCreateSuccessNewUrl() throws Exception {
        when(ctx.formParamAsClass("url", String.class))
                .thenAnswer(i -> stringValidator("https://new-site.com:8443"));

        try (MockedStatic<UrlsRepository> repo = mockStatic(UrlsRepository.class)) {
            repo.when(() -> UrlsRepository.isExist("https://new-site.com:8443")).thenReturn(false);

            UrlsController.create(ctx);

            repo.verify(() -> UrlsRepository.save(argThat(u -> "https://new-site.com:8443".equals(u.getName()))));
            verify(ctx).sessionAttribute("flash", "Страница успешно добавлена");
            verify(ctx).sessionAttribute("flashType", "correct");
            verify(ctx).redirect(NamedRoutes.urlsPath());
        }
    }

    @Test
    void testCreateAlreadyExists() throws Exception {
        when(ctx.formParamAsClass("url", String.class))
                .thenAnswer(i -> stringValidator("http://exists.com"));

        try (MockedStatic<UrlsRepository> repo = mockStatic(UrlsRepository.class)) {
            repo.when(() -> UrlsRepository.isExist("http://exists.com")).thenReturn(true);

            UrlsController.create(ctx);

            verify(ctx).sessionAttribute("flash", "Страница уже существует");
            verify(ctx).sessionAttribute("flashType", "info");
            verify(ctx).redirect(NamedRoutes.urlsPath());
        }
    }

    @Test
    void testCreateInvalidUrl() throws Exception {
        when(ctx.formParamAsClass("url", String.class))
                .thenAnswer(i -> stringValidator("это точно не url"));

        UrlsController.create(ctx);

        verify(ctx).status(400);
        verify(ctx).render(eq("index.jte"), any(Map.class));
    }

    @Test
    void testShowUrlExists() throws Exception {
        Url url = new Url("https://example.com");
        url.setId(10L);

        try (MockedStatic<UrlsRepository> urlRepo = mockStatic(UrlsRepository.class);
             MockedStatic<UrlChecksRepository> checkRepo = mockStatic(UrlChecksRepository.class)) {

            urlRepo.when(() -> UrlsRepository.find(10L)).thenReturn(Optional.of(url));
            Deque<UrlCheck> checks = new ConcurrentLinkedDeque<>();
            checkRepo.when(() -> UrlChecksRepository.getEntitiesByUrlId(10L)).thenReturn(checks);

            when(ctx.pathParamAsClass("id", Long.class)).thenAnswer(i -> longValidator(10L));

            UrlsController.show(ctx);

            verify(ctx).render(eq("urls/show.jte"), argThat(map -> {
                UrlPage page = (UrlPage) map.get("page");
                return page != null && page.getUrl() == url;
            }));
        }
    }

    @Test
    void testShowUrlNotFound() throws Exception {
        try (MockedStatic<UrlsRepository> repo = mockStatic(UrlsRepository.class)) {
            repo.when(() -> UrlsRepository.find(999L)).thenReturn(Optional.empty());
            when(ctx.pathParamAsClass("id", Long.class)).thenAnswer(i -> longValidator(999L));

            assertThrows(NotFoundResponse.class, () -> UrlsController.show(ctx));
        }
    }

    @Test
    void testCheckSuccess() throws Exception {
        Url url = new Url("https://example.com");
        url.setId(7L);

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(200);
        when(response.getBody()).thenReturn(
                "<html><head><title>Title</title><meta name=\"description\" "
                        + "content=\"desc\"></head><body><h1>H1</h1></body></html>"
        );

        GetRequest getRequest = mock(GetRequest.class);
        when(getRequest.asString()).thenReturn(response);

        try (MockedStatic<UrlsRepository> urlRepo = mockStatic(UrlsRepository.class);
             MockedStatic<UrlChecksRepository> checkRepo = mockStatic(UrlChecksRepository.class);
             MockedStatic<kong.unirest.Unirest> unirest = mockStatic(kong.unirest.Unirest.class)) {

            urlRepo.when(() -> UrlsRepository.find(7L)).thenReturn(Optional.of(url));
            unirest.when(() -> kong.unirest.Unirest.get("https://example.com")).thenReturn(getRequest);
            checkRepo.when(() -> UrlChecksRepository.save(any(UrlCheck.class))).thenAnswer(i -> null);

            when(ctx.pathParamAsClass("id", Long.class)).thenAnswer(i -> longValidator(7L));

            UrlsController.check(ctx);

            verify(ctx).sessionAttribute("flash", "Страница успешно проверена");
            verify(ctx).sessionAttribute("flashType", "success");
            verify(ctx).redirect(NamedRoutes.urlPath(7L));
        }
    }

    @Test
    void testCheckFailNetworkError() throws Exception {
        Url url = new Url("https://bad-url.example");
        url.setId(1L);

        try (MockedStatic<UrlsRepository> urlRepo = mockStatic(UrlsRepository.class);
             MockedStatic<kong.unirest.Unirest> unirest = mockStatic(kong.unirest.Unirest.class)) {

            urlRepo.when(() -> UrlsRepository.find(1L)).thenReturn(Optional.of(url));
            unirest.when(() -> kong.unirest.Unirest.get("https://bad-url.example"))
                    .thenThrow(new RuntimeException("No connection"));

            when(ctx.pathParamAsClass("id", Long.class)).thenAnswer(i -> longValidator(1L));

            UrlsController.check(ctx);

            // Исправляем вызовы verify: оба аргумента теперь матчеры
            verify(ctx).sessionAttribute(
                    argThat(name -> "flash".equals(name)),
                    argThat(value -> ((String) value).startsWith("Ошибка при проверке:"))
            );
            verify(ctx).sessionAttribute(
                    argThat(name -> "flashType".equals(name)),
                    argThat(value -> "danger".equals(value))
            );

            verify(ctx).redirect(NamedRoutes.urlPath(1L));
        }
    }
}
