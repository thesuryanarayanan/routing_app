function vehicleTypeEnumToText(vehicleEnum) {
  switch (vehicleEnum) {
    case "PANELVAN":
      return "Panelvan";
    case "LORRY":
      return "Kamyon";
    case "TRUCK":
      return "Tır";
    default:
      return "Araç Tipi Bulunamadı";
  }
}

function dispatchTypeEnumToText(dispatchTypeEnum) {
  switch (dispatchTypeEnum) {
    case "SACK":
      return "Çuval";
    case "BAG":
      return "Torba";
    case "PARCEL":
      return "Koli";
    case "FILE":
      return "Dosya";
    default:
      return "Dosya Tipi Bulunamadı";
  }
}

function deliveryRangeEnumToText(deliveryRangeEnum) {
  switch (deliveryRangeEnum) {
    case "MORNING":
      return "Sabah";
    case "MIDMORNING":
      return "Öğleden Önce";
    case "AFTERNOON":
      return "Öğleden Sonra";
    case "EVENING":
      return "Akşam";
    default:
      return "Teslimat Aralığı Bulunamadı";
  }
}

function deliveryRangeTextToEnum(deliveryRangeEnum) {
  switch (deliveryRangeEnum) {
    case "Sabah":
      return "MORNING";
    case "Öğleden Önce":
      return "MIDMORNING";
    case "Öğleden Sonra":
      return "AFTERNOON";
    case "Akşam":
      return "EVENING";
  }
}

function isWithinBoundingBox(
  pointLatitude,
  pointLongitude,
  latitude1,
  longitude1,
  latitude2,
  longitude2
) {
  var isWithinLatitude =
    pointLatitude <= latitude1 && pointLatitude >= latitude2;
  var isWithinLongitude =
    pointLongitude >= longitude1 && pointLongitude <= longitude2;

  return isWithinLatitude && isWithinLongitude;
}

function getDeliveryRangeHours(deliveryRange) {
  switch (deliveryRange) {
    case "MIDMORNING":
    case "MORNING":
      return [7, 12];
    case "AFTERNOON":
    case "EVENING":
      return [13, 18];
    default:
      return [0, 0];
  }
}

function createDateWithTime(hour, duration) {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth();
  const day = now.getDate();

  const customDate = new Date(year, month, day, hour, 0);

  customDate.setMinutes(customDate.getMinutes() + duration);

  const formattedDay = day.toString().padStart(2, "0");
  const formattedMonth = (month + 1).toString().padStart(2, "0");
  const formattedHour = customDate.getHours().toString().padStart(2, "0");
  const formattedMinute = customDate.getMinutes().toString().padStart(2, "0");

  return `${formattedDay}.${formattedMonth}.${year} ${formattedHour}:${formattedMinute}:00`;
}

function isExistPreferedDeliveryTime(dispatch) {
  return dispatch?.preferFirstDeliveryTime && dispatch?.preferLastDeliveryTime;
}
