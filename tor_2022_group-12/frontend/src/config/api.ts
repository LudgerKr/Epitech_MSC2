import { AxiosRequestConfig } from 'axios'

const API_URL = 'http://localhost:5000'

export const SERVER_URL = API_URL

const defaultRequest: AxiosRequestConfig = {
    url: '',
    data: {},
    method: undefined,
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
    },
}

export const POST_REQUEST: AxiosRequestConfig = {
    ...defaultRequest,
    method: 'POST',
}

export const GET_REQUEST: AxiosRequestConfig = {
    ...defaultRequest,
    method: 'GET',
}

export const PUT_REQUEST: AxiosRequestConfig = {
    ...defaultRequest,
    method: 'PUT',
}

export const PATCH_REQUEST: AxiosRequestConfig = {
    ...defaultRequest,
    method: 'PATCH',
}

export const MULTIPART_FORMDATA_REQUEST: AxiosRequestConfig = {
    headers: {
        'Content-type': 'multipart/form-data',
    },
    method: 'PUT',
}

export const DELETE_REQUEST: AxiosRequestConfig = {
    ...defaultRequest,
    method: 'DELETE',
}
