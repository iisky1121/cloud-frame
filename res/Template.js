(function() {

	var root = this;

	var _ = function(obj) {
		if (obj instanceof _)
			return obj;
		if (!(this instanceof _))
			return new _(obj);
		this._wrapped = obj;
	};
	root._ = _;
	
    _.escape = function (source) {
        return String(source)
            .replace(/&/g,'&amp;')
            .replace(/</g,'&lt;')
            .replace(/>/g,'&gt;')
            .replace(/\\/g,'&#92;')
            .replace(/"/g,'&quot;')
            .replace(/'/g,'&#39;');
    };
	
	_.templateSettings = {
		evaluate : /<%([\s\S]+?)%>/g,
		interpolate : /<%=([\s\S]+?)%>/g,
		escape : /<%-([\s\S]+?)%>/g,
		replace: /<%\+([\s\S]+?)%>/g
	};

	var noMatch = /(.)^/;

	var escapes = {
		"'" : "'",
		'\\' : '\\',
		'\r' : 'r',
		'\n' : 'n',
		'\u2028' : 'u2028',
		'\u2029' : 'u2029'
	};

	var escaper = /\\|'|\r|\n|\u2028|\u2029/g;

	var escapeChar = function(match) {
		return '\\' + escapes[match];
	};
	
	_.a=function(s,replace,args){
		if(!replace){
			args.add(s);
			return "?";
		}
		else {
			return s;
		}
	}
	
	_.template = function(text) {
		settings = _.templateSettings;
		var matcher = RegExp(
				[ 
				  (settings.replace || noMatch).source,
				  (settings.escape || noMatch).source, 
				  (settings.interpolate || noMatch).source, 
				  (settings.evaluate || noMatch).source
				].join('|') + '|$', 'g');
		var index = 0;
		var source = "__p+='";
		text.replace(matcher, function(match, replcae, escape, interpolate, evaluate, offset) {
			source += text.slice(index, offset).replace(escaper, escapeChar);
			index = offset + match.length;

			if (escape) {
				source += "'+\n_.a(((__t=(" + escape + "))==null?'':_.escape(__t)),false,args)+\n'";
			} else if (interpolate) {
				source += "'+\n_.a(((__t=(" + interpolate + "))==null?'':__t),false,args)+\n'";
			} else if (replcae) {
				source += "'+\n_.a(((__t=(" + replcae + "))==null?'':__t),true,args)+\n'";
			} else if (evaluate) {
				source += "';\n" + evaluate + "\n__p+='";
			}
			return match;
		});
		source += "';\n";
		

		
		if (!settings.variable)
			source = 'with(obj||{}){\n' + source + '}\n';

		source = "var __t,__p='',__j=Array.prototype.join," + "print=function(){__p+=__j.call(arguments,'');};\n" + source + 'return __p;\n';
		
		try {
			var render = new Function(settings.variable || 'obj', '_','args', source);
		} catch (e) {
			e.source = source;
			throw e;
		}
		var template = function(data,args) {
			return render.call(this, data, _,args);
		};
		var argument = settings.variable || 'obj';
		template.source = 'function(' + argument + '){\n' + source + '}';

		return template;
	};
}.call(this));