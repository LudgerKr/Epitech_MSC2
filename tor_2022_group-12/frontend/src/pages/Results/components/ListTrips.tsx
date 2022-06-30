import { Grid, Typography } from '@mui/material'
import { DataGrid, GridColDef, GridValueGetterParams } from '@mui/x-data-grid'
import React, { FunctionComponent } from 'react'
import { useTranslation } from 'react-i18next'
import useLayoutsStyles from 'src/assets/layout'
import { Route } from 'src/types/Route'

interface Props {
    routes: Route[]
}
const ListTrips: FunctionComponent<Props> = ({ routes }) => {
    const styles = useLayoutsStyles()
    const { t } = useTranslation()

    const getDestination1 = (params: GridValueGetterParams<Route, any>) => {
        if (params.row.destination2 && params.row.destination1) return params.row.destination1
        return params.row.destination2
    }
    const getDestination2 = (params: GridValueGetterParams<Route, any>) => {
        if (params.row.destination2 && params.row.destination1) return params.row.destination2
        return params.row.destination1
    }

    const columns: GridColDef[] = [
        { field: 'id', headerName: t('referenceRow'), width: 200 },
        { field: 'departure', headerName: t('departureRow'), width: 200 },
        {
            field: 'destination1',
            headerName: t('destination1Row'),
            width: 150,
            valueGetter: getDestination1,
        },
        { field: 'destination2', headerName: t('destination2Row'), width: 200, valueGetter: getDestination2 },
        { field: 'duration', headerName: t('duration'), width: 200 },
    ]

    return (
        <Grid container className={styles.grid_container}>
            <Grid item xs={12}>
                <Typography variant="h2" component="div" gutterBottom className={styles.title}>
                    {t('allRoutes')}
                </Typography>
            </Grid>
            <Grid item xs={12} style={{ height: 300, width: '100%' }}>
                <DataGrid rows={routes} columns={columns} className={styles.datagrid} />
            </Grid>
        </Grid>
    )
}
export default ListTrips
