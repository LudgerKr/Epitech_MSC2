import { Grid, Typography } from '@mui/material'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableRow from '@mui/material/TableRow'
import React, { FunctionComponent } from 'react'
import { useTranslation } from 'react-i18next'
import useLayoutsStyles from 'src/assets/layout'
import { Route } from 'src/types/Route'

interface Props {
    route: Route | undefined
}

const ShortestRoute: FunctionComponent<Props> = ({ route }) => {
    const styles = useLayoutsStyles()
    const { t } = useTranslation()

    return (
        <Grid container>
            <Grid item xs={12}>
                <Typography variant="h2" component="div" gutterBottom className={styles.titleBlack}>
                    {t('fastestRoute')}
                </Typography>
            </Grid>
            <Grid item xs={12}>
                <TableContainer component={Paper}>
                    <Table sx={{ minWidth: 400 }} aria-label="simple table">
                        <TableBody>
                            <TableRow key={route?.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                {route ? (
                                    <>
                                        <TableCell component="th" scope="route">
                                            {route?.duration}
                                        </TableCell>
                                        <TableCell align="center">{route?.id}</TableCell>
                                        <TableCell align="center">{route?.departure}</TableCell>
                                        <TableCell align="center">{'->'}</TableCell>
                                        <TableCell align="center">{route?.destination1}</TableCell>
                                        {route?.destination2 && (
                                            <>
                                                <TableCell align="center">{'->'}</TableCell>
                                                <TableCell align="center">{route?.destination2}</TableCell>
                                            </>
                                        )}
                                    </>
                                ) : (
                                    <TableCell align="center">{t('noRoute')}</TableCell>
                                )}
                            </TableRow>
                        </TableBody>
                    </Table>
                </TableContainer>
            </Grid>
        </Grid>
    )
}
export default ShortestRoute
