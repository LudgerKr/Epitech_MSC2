import ArrowForwardIcon from '@mui/icons-material/ArrowForward'
import { Grid, IconButton, TextField } from '@mui/material'
import { FunctionComponent, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Outlet, useNavigate } from 'react-router-dom'
import useLayoutsStyles from 'src/assets/layout'
import { Micro } from 'src/components'

const RightPart: FunctionComponent = () => {
    const styles = useLayoutsStyles()
    const navigate = useNavigate()
    const { t } = useTranslation()

    const [startPoint, setStartPoint] = useState('')
    const [endPoint, setEndPoint] = useState('')

    const [transcript, setTranscript] = useState('')

    const onFinalTranscript = () => {
        navigate('/results', { state: transcript })
    }

    return (
        <Grid container className={styles.grid_blue}>
            <Grid item xs={12}>
                <TextField
                    sx={{ input: { color: 'white' } }}
                    fullWidth
                    value={startPoint}
                    label={t('departure')}
                    id="starting-point"
                    onChange={e => setStartPoint(e.target.value)}
                    variant="outlined"
                    color="primary"
                    InputLabelProps={{
                        classes: {
                            root: styles.cssLabel,
                        },
                    }}
                />
            </Grid>
            <Grid item xs={12}>
                <TextField
                    sx={{ input: { color: 'white' } }}
                    fullWidth
                    value={endPoint}
                    label={t('destination')}
                    id="end-point"
                    onChange={e => setEndPoint(e.target.value)}
                    margin="normal"
                    variant="outlined"
                    color="primary"
                    InputLabelProps={{
                        classes: {
                            root: styles.cssLabel,
                        },
                    }}
                />
            </Grid>
            <Grid item xs={6} className={styles.align_item}>
                <Micro onFinalTranscript={setTranscript} />
            </Grid>
            <Grid item xs={6} className={styles.align_item}>
                <IconButton color="primary" onClick={() => onFinalTranscript()}>
                    <ArrowForwardIcon color="primary" />
                </IconButton>
            </Grid>
            <Outlet />
        </Grid>
    )
}
export default RightPart
