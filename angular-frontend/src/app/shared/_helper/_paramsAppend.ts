import { HttpParams } from "@angular/common/http";
import { ShopParams } from "../_models/ShopParams";

export const _paramsAppend = (paramsData: ShopParams) => {
    let params = new HttpParams();

    if (!paramsData) return params;
  
    Object.keys(paramsData).forEach((element) => {
      if (paramsData[element]) {
        params = params.append(element, paramsData[element]);
      }
    });
    return params;
  };