import i18next from 'i18next'
import LanguageDetector from 'i18next-browser-languagedetector'
import en from 'src/locales/en'
import fr from 'src/locales/fr'

i18next.use(LanguageDetector).init({
    // we init with resources
    resources: {
        en: {
            translations: {
                ...en,
            },
        },
        fr: {
            translations: {
                ...fr,
            },
        },
    },
    fallbackLng: 'fr',

    // have a common namespace used around the full app
    ns: ['translations'],
    defaultNS: 'translations',

    interpolation: {
        formatSeparator: ',',
    },

    react: {
        wait: false,
    },
})

export const currentLanguageCode: string = i18next.language.substring(0, 2)

export default i18next
