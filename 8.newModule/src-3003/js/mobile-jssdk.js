(function (global) {
    var api = {
        ExAddressbookResource: {
            xntlListDate: function (_params, _body, options) {
                return SeeyonApi.Rest.post('addbook/xntlListDate', _params, _body, cmp.extend({}, options))
            },
        }
    };
    global.SeeyonApi = global.SeeyonApi || {};
    for (var key in api) {
        global.SeeyonApi[key] = api[key];
    }
})(this);