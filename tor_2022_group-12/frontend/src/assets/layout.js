import makeStyles from '@mui/styles/makeStyles'

const useLayoutsStyles = makeStyles(theme => ({
    /*** Page Home ***/
    grid_container: {
        height: '100%',
    },
    grid_left: {
        alignSelf: 'center',
    },
    grid_right: {
        alignSelf: 'center',
    },
    grid_middle: {
        alignSelf: 'center',
    },

    /*** Component LeftPart ***/
    grid_yellow: {
        height: '300px',
        backgroundColor: '#ffc300',
        padding: '30px 30px 10px 30px',
    },
    align_item: {
        textAlign: 'center',
        marginTop: '10px',
    },
    textField_left: {
        color: '#003566',
    },
    /*** Component RightPart ***/
    grid_blue: {
        height: '300px',
        backgroundColor: '#003566',
        padding: '30px 30px 10px 30px',
    },
    textField_right: {
        color: '#ffc300',
    },
    /*** TextField ***/
    cssLabel: {
        color: '#ffc300',
    },
    cssLabel_blue: {
        color: '#003566',
    },
    /*** SearchBar ***/
    container_searchbar: {
        backgroundColor: '#003566',
        border: '1px solid #003566',
        padding: '20px',
    },
    /*** NavBar ***/
    navbar: {
        zIndex: '3',
    },
    /*** ***/
    title: {
        fontSize: '30px',
        textAlign: 'left',
        color: 'white',
        textTransform: 'uppercase',
        margin: '20px 0px 20px 0px',
    },
    titleBlack: {
        fontSize: '30px',
        textAlign: 'left',
        color: 'black',
        textTransform: 'uppercase',
        margin: '20px 0px 20px 0px',
    },
    container_shortestRoute: {
        backgroundColor: '#ffc300',
        padding: '20px',
        marginTop: '10px',
        marginBottom: '10px',
    },
    container_listTrips: {
        backgroundColor: '#003566',
        padding: '20px',
    },
    datagrid: {
        background: 'white',
    },
}))

export default useLayoutsStyles
