# ANalysis Access Live Concordia

## REST API


**ANalaysis Access Live** gives you access to the capacity of the campus and the individual buildings 
```
Method: GET

/capacity/buildings
/capacity/buildings/{building}
```


**ANalaysis Access Live** gives you access to the realtime total visitors of the campus and the individual buildings 
```
Method: GET

/total_visitors/buildings
/total_visitors/buildings/{building}
```


**ANalaysis Access Live** gives you access to the realtime booking percentage of the campus and the individual buildings
```
Method: GET

/booking/buildings
/booking/buildings/{building}
```


**ANalaysis Access Live** lets you book access to a specified building or to a specific room
```
Method: PUT

/book/buildings/{building}
/book/buildings/{building}/{room}
```


**ANalaysis Access Live** lets you unbook access to a specified building or to a specific room
```
Method: PUT

/unbook/buildings/{building}
/unbook/buildings/{building}/{room}
```
