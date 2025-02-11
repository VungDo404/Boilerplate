import {
    APP_INITIALIZER,
    ApplicationConfig,
    importProvidersFrom, inject,
    provideAppInitializer,
    provideZoneChangeDetection
} from '@angular/core';

import { provideAnimationsAsync } from "@angular/platform-browser/animations/async";
import { providePrimeNG } from "primeng/config";
import Aura from '@primeng/themes/aura';
import { provideRouter } from "@angular/router";
import { ROUTE } from "./root.route";
import { HTTP_INTERCEPTORS, HttpClient, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { TranslateLoader, TranslateModule } from "@ngx-translate/core";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import { LanguageHeaderService } from "./shared/interceptor/language-header.service";
import { LanguageService } from "./shared/service/language.service";

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

export const config: ApplicationConfig = {
    providers: [
        provideRouter(ROUTE),
        provideZoneChangeDetection({eventCoalescing: true}),
        provideAnimationsAsync(),
        providePrimeNG({
            theme: {
                preset: Aura,
                options: {
                    darkModeSelector: '.my-app-dark'
                }
            }
        }),
        provideHttpClient(withInterceptorsFromDi()),
        importProvidersFrom(TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        })),
        {
            provide: HTTP_INTERCEPTORS,
            useClass: LanguageHeaderService,
            multi: true
        },
        provideAppInitializer(() =>{
            const languageService = inject(LanguageService);
            return languageService.initializeLanguage();
        })

    ]
};
