import ArrowForwardIcon from '@mui/icons-material/ArrowForward'
import { Grid, IconButton, Link, TextField, Typography } from '@mui/material'
import React, { FunctionComponent, useState } from 'react'
import { useTranslation } from 'react-i18next'
import useLayoutsStyles from 'src/assets/layout'
import { Micro } from 'src/components'

interface Props {
    onFinalTranscript: (transcript: string) => void
}

const SearchBar: FunctionComponent<Props> = ({ onFinalTranscript }) => {
    const styles = useLayoutsStyles()
    const { t } = useTranslation()

    const [transcript, setTranscript] = useState('')

    return (
        <Grid container className={styles.container_searchbar}>
            <Grid item xs={12}>
                <Typography variant="h2" component="div" gutterBottom className={styles.title}>
                    {t('research')}
                </Typography>
            </Grid>
            <Grid item xs={6}>
                <TextField
                    sx={{ input: { color: 'white' } }}
                    fullWidth
                    label={t('departure')}
                    id="starting-point"
                    variant="outlined"
                    color="primary"
                    InputLabelProps={{
                        classes: {
                            root: styles.cssLabel,
                        },
                    }}
                />
            </Grid>
            <Grid item xs={6}>
                <TextField
                    sx={{ input: { color: 'white' } }}
                    fullWidth
                    label={t('destination')}
                    id="end-point"
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
                <IconButton color="primary" component={Link} onClick={() => onFinalTranscript(transcript)}>
                    <Link>
                        <ArrowForwardIcon color="primary" />
                    </Link>
                </IconButton>
            </Grid>
        </Grid>
    )
}
export default SearchBar
