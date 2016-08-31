/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
(function(){

	var util = {
		constants : {
			statusOptions : [
				{ name : "Segnazione ricevuta" , 					value : 0},
				{ name : "Pratica presa in carica" , 			value : 1},
				{ name : "Verifica identità segnalante" , value : 2},
				{ name : "Verifica di ammissibilità" , 		value : 3},
				{ name : "Apertura indagine" , 						value : 4},
				{ name : "Pratica non ammissibile" , 			value : 5},
				{ name : "Pratica risolta" , 							value : 6},
				{ name : "Pratica chiusa" , 							value : 7}
			],

			otherRecipientOptions : [
				{ name : "Resp. prevenzione della corruzione" } ,
				{ name : "Corte dei Conti" } ,
				{ name : "Autorità Giudiziaria" }
			],

			reasonOptions  : [
				{ name : "E' penalmente rilevante", mtemplate: 'default' } ,
				{ name : "Viola il Codice di comportamento o altre disposizioni sanzionabili in via disciplinare" , mtemplate: 'default' } ,
				{ name : "Arreca un danno patrimoniale all’ente o altra amministrazione", mtemplate: 'default'  } ,
				{ name : "Arreca un danno all’immagine dell’amministrazione", mtemplate: 'default' } ,
				{ name : "Viola le norme ambientali e di sicurezza sul lavoro", mtemplate: 'default'  } ,
				{ name : "Costituisce un caso di malagestione delle risorse pubbliche (sprechi,mancato rispetto dei termini procedimentali, ecc.)" , mtemplate: 'default' } ,
				{ name : "Costituisce una misura discriminatoria nei confronti del dipendente pubblico che ha segnalato illecito", mtemplate: 'default' } ,
				{ name : "Altro", mtemplate: 'default' },

				{ name : "penalmente rilevanti" , mtemplate : 'simple' } ,
				{ name : "poste in essere in violazione dei Codici di comportamento o altre disposizioni sanzionabili in via disciplinare" , mtemplate : 'simple' } ,
				{ name : "suscettibili di arrecare un pregiudizio patrimoniale all’amministrazione di appartenenza o ad altro ente pubblico" , mtemplate: 'simple' } ,
				{ name : "suscettibili di arrecare un pregiudizio alla immagine dell’amministrazione" , mtemplate : 'simple' } ,
				{ name : "altro", mtemplate: 'simple' }
			],

			areaOptions: [
				{ name : "Reclutamento del personale" } ,
				{ name : "Contratti" } ,
				{ name : "Concessione di vantaggi economici comunque denominati" } ,
				{ name : "Autorizzazioni" } ,
				{ name : "Ispezioni" } ,
				{ name : "Altro" } 
			],

			sectorOptions : [
				{ name : "Sanità" } ,
				{ name : "Gestione del territorio" } ,
				{ name : "Protezione ambientale" } ,
				{ name : "Gestione dei rifiuti" } ,
				{ name : "Trasporti e Viabilità" } ,
				{ name : "Ordine pubblico" } ,
				{ name : "Telecomunicazioni" } ,
				{ name : "Politiche agricole e forestali" } ,
				{ name : "Beni e attività culturali" } ,
				{ name : "Sviluppo economico" } ,
				{ name : "Istruzione e Formazione" } ,
				{ name : "Gestione Finanziaria" } ,
				{ name : "Altro" }
			],

			placeTypeOptions : [
				{ name : "UFFICIO", value : "Interno" } ,
				{ name : "ALL’ESTERNO DELL’UFFICIO", value : "Esterno" }
			]
		}
	};

	util.base64toBlob = function(b64Data, contentType) {
		contentType = contentType || '';
		var sliceSize = 512;
		b64Data = b64Data.replace(/^[^,]+,/, '');
		b64Data = b64Data.replace(/\s/g, '');
		var byteCharacters = window.atob(b64Data);
		var byteArrays = [];

		for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
			var slice = byteCharacters.slice(offset, offset + sliceSize);

			var byteNumbers = new Array(slice.length);
			for (var i = 0; i < slice.length; i++) {
				byteNumbers[i] = slice.charCodeAt(i);
			}

			var byteArray = new Uint8Array(byteNumbers);

			byteArrays.push(byteArray);
		}

		var blob = new Blob(byteArrays, {type: contentType});
		return blob;
	};
	
	app.util = util;

	if (!Array.prototype.find) {
		Array.prototype.find = function(predicate) {
			if (this === null) {
				throw new TypeError('Array.prototype.find called on null or undefined');
			}
			if (typeof predicate !== 'function') {
				throw new TypeError('predicate must be a function');
			}
			var list = Object(this);
			var length = list.length >>> 0;
			var thisArg = arguments[1];
			var value;

			for (var i = 0; i < length; i++) {
				value = list[i];
				if (predicate.call(thisArg, value, i, list)) {
					return value;
				}
			}
			return undefined;
		};
	}

})()

