import MicIcon from '@mui/icons-material/Mic'
import ReplayIcon from '@mui/icons-material/Replay'
import StopIcon from '@mui/icons-material/Stop'
import { Grid, IconButton } from '@mui/material'
import { FunctionComponent, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import SpeechRecognition, { useSpeechRecognition } from 'react-speech-recognition'
import useLayoutsStyles from 'src/assets/layout'

interface Props {
    onFinalTranscript: (transcript: string) => void
}

const Micro: FunctionComponent<Props> = ({ onFinalTranscript }) => {
    const styles = useLayoutsStyles()
    const { i18n } = useTranslation()

    const commands = [
        {
            command: 'Annuler',
            callback: () => resetTranscript(),
        },
    ]
    const { transcript, interimTranscript, finalTranscript, resetTranscript, listening } = useSpeechRecognition({
        commands,
    })

    useEffect(() => {
        if (finalTranscript !== '') {
            console.log('Got final result:', finalTranscript)
            onFinalTranscript(finalTranscript)
        }
    }, [onFinalTranscript, finalTranscript])

    if (!SpeechRecognition.browserSupportsSpeechRecognition()) {
        console.log('Your browser does not support speech recognition software! Try Chrome desktop, maybe?')
    }
    const listenContinuously = () => {
        SpeechRecognition.startListening({
            continuous: true,
            language: i18n.language === 'fr' ? 'fr-FR' : 'en-GB',
        })
    }

    return (
        <Grid container>
            <Grid item xs={12} className={styles.align_item}>
                {listening ? (
                    <IconButton color="primary" onClick={SpeechRecognition.stopListening}>
                        <StopIcon />
                    </IconButton>
                ) : (
                    <div>
                        <IconButton color="primary" onClick={listenContinuously}>
                            <MicIcon />
                        </IconButton>
                        <IconButton color="primary" onClick={resetTranscript}>
                            <ReplayIcon />
                        </IconButton>
                    </div>
                )}
                <div>
                    <div>
                        <span>{transcript}</span>
                    </div>
                </div>
            </Grid>
        </Grid>
    )
}
export default Micro
