import multiprocessing
import os
from dotenv import load_dotenv

load_dotenv()
PORT = os.getenv("PORT")
bind = f"127.0.0.1:{PORT}"
workers = multiprocessing.cpu_count() * 2 + 1
reload = True
daemon = True
loglevel = "debug"
accesslog = "travel_order.log"
access_log_format = "{\"remote_ip\":\"%(h)s\",\"request_id\":\"%({X-Request-Id}i)s\",\"response_code\":\"%(s)s\",\"request_method\":\"%(m)s\",\"request_path\":\"%(U)s\",\"request_querystring\":\"%(q)s\",\"request_timetaken\":\"%(D)s\",\"response_length\":\"%(B)s\"}"