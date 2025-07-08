import mitt from 'mitt';

type Events = {
  'productBatch-added': void;
  'product-added': void;
};

export const eventBus = mitt<Events>();
