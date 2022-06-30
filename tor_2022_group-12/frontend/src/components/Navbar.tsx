import HomeIcon from '@mui/icons-material/Home'
import { AppBar, IconButton, Toolbar } from '@mui/material'
import React, { FunctionComponent, useState } from 'react'
import ReactFlagsSelect from 'react-flags-select'
import { useTranslation } from 'react-i18next'
import { Link, Outlet, useLocation } from 'react-router-dom'
import useLayoutsStyles from 'src/assets/layout'
import { currentLanguageCode } from 'src/config/i18n'

const Navbar: FunctionComponent = () => {
    const styles = useLayoutsStyles()
    const location = useLocation()
    const { t, i18n } = useTranslation()

    const [selected, setSelected] = useState(
        currentLanguageCode === 'en' ? 'GB' : currentLanguageCode.toLocaleUpperCase()
    )

    const changeLanguage = (code: string) => {
        const cc = code === 'GB' ? 'en' : code.toLocaleLowerCase()

        i18n.changeLanguage(cc, (err, tr) => {
            tr('key')
        })
        setSelected(code)
    }

    return (
        <AppBar position="static" className="styles.navbar">
            <Toolbar>
                <IconButton size="large" color="secondary" edge="start" aria-label="home" sx={{ mr: 2 }}>
                    <Link to="/">
                        <HomeIcon sx={{ color: '#003566' }} />
                    </Link>
                </IconButton>
                <ReactFlagsSelect
                    countries={['GB', 'FR']}
                    placeholder=""
                    fullWidth
                    showOptionLabel={false}
                    showSelectedLabel={false}
                    selected={selected}
                    onSelect={(code: string) => changeLanguage(code)}
                />
            </Toolbar>
            <Outlet />
        </AppBar>
    )
}
export default Navbar
