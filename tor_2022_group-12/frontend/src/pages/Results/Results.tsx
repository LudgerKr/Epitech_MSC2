import { Grid } from '@mui/material'
import axios from 'axios'
import { FunctionComponent, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation } from 'react-router-dom'
import SpeechRecognition from 'react-speech-recognition'
import useLayoutsStyles from 'src/assets/layout'
import { POST_REQUEST, SERVER_URL } from 'src/config/api'
import { Route } from 'src/types/Route'
import { ListTrips, SearchBar, ShortestRoute } from './components'

const Results: FunctionComponent = () => {
    const styles = useLayoutsStyles()
    const { t, i18n } = useTranslation()
    const { state } = useLocation()

    useEffect(() => {
        SpeechRecognition.stopListening()
    }, [])

    const [shortestRoute, setShortestRoute] = useState<Route>()
    const [routes, setRoutes] = useState<Route[]>([])

    useEffect(() => {
        if (state) {
            axios.post(`${SERVER_URL}/travel`, { [i18n.language]: state }, POST_REQUEST).then(res => {
                if (Array.isArray(res.data) && res.data.length) {
                    setShortestRoute({
                        ...res?.data?.[0],
                        duration: `${Math.floor(res.data?.[0].duration / 60)}h${res.data[0].duration % 60}`,
                    })
                    setRoutes(
                        res.data.reduce(
                            (prev: Route[], currentItem: Route, index: number) =>
                                index > 0
                                    ? [
                                          ...prev,
                                          {
                                              ...currentItem,
                                              duration: `${Math.floor(currentItem.duration / 60)}h${
                                                  currentItem.duration % 60
                                              }`,
                                          },
                                      ]
                                    : [...prev],
                            []
                        )
                    )
                } else {
                    setShortestRoute(undefined)
                    setRoutes([])
                }
            })
        }
    }, [state, i18n.language])

    const onFinalTranscript = (transcript: string) => {
        SpeechRecognition.stopListening()
        axios.post(`${SERVER_URL}/travel`, { [i18n.language]: transcript }, POST_REQUEST).then(res => {
            if (Array.isArray(res.data) && res.data.length) {
                setShortestRoute({
                    ...res?.data?.[0],
                    duration: `${Math.floor(res.data?.[0].duration / 60)}h${res.data[0].duration % 60}`,
                })
                setRoutes(
                    res.data.reduce(
                        (prev: Route[], currentItem: Route, index: number) =>
                            index > 0
                                ? [
                                      ...prev,
                                      {
                                          ...currentItem,
                                          duration: `${Math.floor(currentItem.duration / 60)}h${
                                              currentItem.duration % 60
                                          }`,
                                      },
                                  ]
                                : [...prev],
                        []
                    )
                )
            } else {
                setShortestRoute(undefined)
                setRoutes([])
            }
        })
    }

    return (
        <Grid container className={styles.grid_container}>
            <Grid item xs={12} className={styles.grid_middle}>
                <SearchBar onFinalTranscript={onFinalTranscript} />
            </Grid>
            <Grid item xs={12} className={styles.container_shortestRoute}>
                <ShortestRoute route={shortestRoute} />
            </Grid>
            <Grid item xs={12} className={styles.container_listTrips}>
                <ListTrips routes={routes} />
            </Grid>
        </Grid>
    )
}
export default Results
