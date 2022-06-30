import { Grid, Typography } from '@mui/material'
import React, { FunctionComponent } from 'react'
import { useTranslation } from 'react-i18next'
import useLayoutsStyles from 'src/assets/layout'

const LeftPart: FunctionComponent = () => {
    const styles = useLayoutsStyles()
    const { t } = useTranslation()

    return (
        <Grid container className={styles.grid_yellow}>
            <Grid item>
                <Typography variant="h2" component="div" gutterBottom className={styles.textField_left}>
                    {t('prepareTrip')}
                </Typography>
            </Grid>
        </Grid>
    )
}
export default LeftPart
