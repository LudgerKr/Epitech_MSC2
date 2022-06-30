import { StyledEngineProvider } from '@mui/material'
import { createTheme, ThemeProvider } from '@mui/material/styles'
import React from 'react'
import ReactDOM from 'react-dom'
import { I18nextProvider } from 'react-i18next'
import { HashRouter as Router, Route, Routes } from 'react-router-dom'
import i18n from 'src/config/i18n'
import 'src/index.css'
//import AuthRoute from 'src/routes/AuthRoute'
//import indexRoutes from 'src/routes/index'
import { Home, Results } from 'src/pages'
import { Navbar } from './components'

const theme = createTheme({
    palette: {
        primary: {
            main: '#ffc300',
        },
        secondary: {
            main: '#003566',
        },
        success: {
            main: '#003566',
        },
    },
})

ReactDOM.render(
    <ThemeProvider theme={theme}>
        <I18nextProvider i18n={i18n}>
            <StyledEngineProvider injectFirst>
                <div style={{ height: '100vh', display: 'flex', flexDirection: 'column' }}>
                    <Router>
                        <Navbar />

                        <Routes>
                            {/*indexRoutes.map(({ path, component, exact, type }, key) => (
                                    <AuthRoute path={path} exact={exact} component={component} key={key} type={type} />
                                ))*/}
                            <Route path="/" element={<Home />} />
                            <Route path="/results" element={<Results />} />
                        </Routes>
                    </Router>
                </div>
            </StyledEngineProvider>
        </I18nextProvider>
    </ThemeProvider>,
    document.getElementById('root')
)
