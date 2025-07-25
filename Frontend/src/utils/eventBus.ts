import mitt from 'mitt';

type Events = {
  'productBatch-added': void;
  'product-added': void;
  'sale-order-added': void;
  'inventory-checked': void;
};

export const eventBus = mitt<Events>();
