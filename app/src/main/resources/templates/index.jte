@import hexlet.code.dto.BasePage
@param BasePage page

@template.layout.page(
content = @`
    @if(page.getFlash() != null)
        <div class="alert alert-warning" role="alert">
            <p class="m-0">${page.getFlash()}</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Закрыть"></button>
        </div>
    @endif
    <section>
    <div class="container text-center">
        <div class="row">
            <div class="col">
                <h1>Анализатор страниц</h1>
                <p class="lead">Бесплатно проверяйте сайты на SEO пригодность</p>
                <form action="/urls" method="post" class="rss-form text-body">
                    <div class="row">
                        <div class="col">
                            <div class="form-floating">
                                <input id="url-input" autofocus type="text" required name="url" aria-label="url"
                                       class="form-control w-100" placeholder="ссылка" autocomplete="off">
                                <label for="url-input">Ссылка</label>
                            </div>
                        </div>
                        <div class="col-auto">
                            <button type="submit" class="h-100 btn btn-lg btn-primary px-sm-5">Проверить</button>
                        </div>
                    </div>
                </form>
                <p class="mt-2 mb-0 text-muted">Пример: https://www.example.com</p>
            </div>
        </div>
    </div>
    </section>
`
)
