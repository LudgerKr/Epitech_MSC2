import { Grid } from '@mui/material'
import { FunctionComponent } from 'react'
import { LeftPart, RightPart } from './components'
import useLayoutsStyles from 'src/assets/layout'

const Home: FunctionComponent = () => {
    const styles = useLayoutsStyles()

    return (
        <Grid container className={styles.grid_container}>
            <Grid item sm={6} className={styles.grid_left}>
                <LeftPart />
            </Grid>
            <Grid item sm={6} className={styles.grid_right}>
                <RightPart />
            </Grid>
        </Grid>
    )
}
export default Home
