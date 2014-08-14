$(function () {
    "use strict";
    
    var content = $('#content');
    var input = $('#input');
    var status = $('#status');
    var socket = $.atmosphere;
    var request = { url: '/as/stock/update',
                    contentType : "application/json",
                    logLevel : 'debug',
                    transport : 'websocket' ,
                    fallbackTransport: 'long-polling'};
    
    var dataView;
    var data = [];
    var grid;
    
    request.onOpen = function(response) {
        content.html($('<p>', { text: 'connected using ' + response.transport }));
        input.removeAttr('disabled').focus();
        status.text('stock code:');
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = jQuery.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }
        
        handleMessage(json);
    };

    request.onClose = function(response) {
    }

    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };

    var subSocket = socket.subscribe(request);

    input.keydown(function(e) {
        if (e.keyCode === 13) {
            var msg = $(this).val();

            subSocket.push(jQuery.stringifyJSON({ stock: msg }));
            $(this).val('');

            input.attr('disabled', 'disabled');
        }
    });
    

    function addMessage(author, message, color, datetime) {
        content.append('<p><span style="color:' + color + '">' + author + '</span> @ ' +
            + (datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours()) + ':'
            + (datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes())
            + ': ' + message + '</p>');
    }
       
    $(window).unload(function() {
    	socket.unsubscribe();
    });
    
    $(window).resize(function() {
    	grid.resizeCanvas()
    });
    
    var handlers = [                    
                    {
                    	canHandle : function(m) {
                    		return m.stock!=undefined;
                    	},
                    	handle : function(m) {
                            input.removeAttr('disabled');

                            var date = typeof(m.time) == 'string' ? parseInt(m.time) : m.time;
                            addMessage(m.stock, m.text, 'blue', new Date(date))                    		
                    	}
                    },
                     {
                    	canHandle: function(m) {
                    		return m.price!=undefined;
                    	},
                    	handle: function(m) {
                    		//$('#update').append('<p>' + JSON.stringify(m) +'</p>');
                    		if(grid == undefined) return;
                    		m.price.priceChange = m.priceChange;
                    		m.price.volChange = m.volChange;
                    		var sortCols = grid.getSortColumns();
	                    	var i=0;
                    		if(sortCols.length > 0) { 
	                    		var cols = new Array();
		                    	for(i=0;i<sortCols.length;i++) {
		                    	    cols[i] = new Object();
		                    	    cols[i]['sortCol'] = grid.getColumns()[grid.getColumnIndex(sortCols[i].columnId)];
		                    	    cols[i]['sortAsc'] = sortCols[i].sortAsc;
		                    	}
		                    	var loc = -1;
		                    	for(i=0;i<data.length;i++) { // keep sorted order
		                    		 if(data[i].symbol == m.price.symbol) {
		                    			 loc = i;
		                    			 break;
		                    		 }
		                    	}
		                    	if(loc >= 0)
		                    		data.splice(loc,1);
		                    	loc = -1;
		                    	for(i=0;i<data.length;i++) {
		                    		 if(sortData(cols)(m.price,data[i])<0) {
		                    			 loc = i;
		                    			 break;
		                    		 }		
		                    	}
		                    	if(i == data.length) {
		                    		loc = i;
		                    	}
		                    	data.splice(loc,0,m.price);
                    		} else {
		                    	for(i=0;i<data.length;i++) {
		                    		 if(data[i].symbol == m.price.symbol) {
		                    			 data.splice(i,1,m.price);
		                    			 break;
		                    		 }
		                    	}
		                    	if(i==data.length) {
		                    		data.splice(i,data.length,m.price);
		                    	}
                    		}
	                    	
	                    	updateView();	
                    	}
                    }
                    
                    ];
    
    var updating = 0;
    
    function updateView() {
    	if(updating == 1) return;
    	updating = 1;
    	setTimeout(doUpdateView, 200);
    }
    
    function doUpdateView() {
    	updating = 0;
    	dataView.refresh();
    }
    
    function handleMessage(msg) {
       for(var i=0;i<handlers.length;i++) {
    	   	if(handlers[i].canHandle(msg)) {
				handlers[i].handle(msg);
			}
       }
    }
    
    function sortData(cols) {
        return function(dataRow1, dataRow2) { 
        	for (var i = 0, l = cols.length; i < l; i++) {
        	   
	          var field = cols[i].sortCol.field;
	          var sign = cols[i].sortAsc ? 1 : -1;
	          var value1 = dataRow1[field], value2 = dataRow2[field];
	          if(!isNaN(Number(value1)) && !isNaN(Number(value2))) {
	        	  value1 = Number(value1);
	        	  value2 = Number(value2);
	          } 			          
	          var result = (value1 == value2 ? 0 : (value1 > value2 ? 1 : -1)) * sign;
	          if (result != 0) {
	            return result;
	          }
	        }
	        return 0;
        }
      }
    
    function percentageFormatter(row, cell, value, columnDef, dataContext) {
        if(value > 0) {
        	return "<div class='pos'>" + (value*100).toFixed(2) + "%</div>";
        } else if (value < 0){
        	return "<div class='neg'>" + (value*100).toFixed(2) + "%</div>"; 
        } else {
        	return "<div>-</div>";
        }
    }

    
    function createGrid() {
	    var columns = [
	      { id: "symbol", name: "symbol", field: "symbol", sortable: true },
	      { id: "mid", name: "mid", field: "mid", sortable: true },
	      { id: "bid", name: "bid", field: "bid", sortable: true },
	      { id: "ask", name: "ask", field: "ask", sortable: true },
	      { id: "vol", name: "volume", field: "vol", sortable: true },
	      { id: "priceChange", name: "price change", field: "priceChange", sortable: true, formatter:percentageFormatter },
	      { id: "volChange", name: "volume change", field: "volChange", sortable: true, formatter:percentageFormatter },
	      { id: "time", name: "last update", field: "time", sortable: true,formatter:Slick.Formatters.Date },
	    ];
		
	    var options = {
	      enableCellNavigation: true,
	      enableColumnReorder: false,
	      multiColumnSort: true,
	      autoHeight:true,
	      forceFitColumns: true
	    };
	
	    $(function () {
	      dataView = new Slick.Data.DataView({ inlineFilters: true });
	      grid = new Slick.Grid("#myGrid", dataView, columns, options);	      

	      // wire up model events to drive the grid
	      dataView.onRowCountChanged.subscribe(function (e, args) {
	        grid.updateRowCount();
	        grid.render();
	      });

	      dataView.onRowsChanged.subscribe(function (e, args) {
	        grid.invalidateRows(args.rows);
	        grid.render();
	      });
	      
			grid.onSort.subscribe(function (e, args) {
			      var cols = args.sortCols;
			
			      data.sort(sortData(cols));
			      dataView.refresh();
			      //grid.invalidate();
			      //grid.render();
			    });
	      
	      dataView.beginUpdate();
	      dataView.setItems(data);
	      dataView.endUpdate();
	    })
	    

    }
    
    $(document).ready( 
    	function() {
    		createGrid();
    	}
    );
});

